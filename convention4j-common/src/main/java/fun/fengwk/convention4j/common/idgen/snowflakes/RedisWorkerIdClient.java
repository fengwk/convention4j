package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.idgen.ClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RedisWorkerIdClient为所有连接到同一redis库的客户端提供[0..1024)中的某一不重复workerId。
 * 算法采用最终一致，对于主从切换导致的数据不一致，客户端会有一个发现周期，由KEEPALIVE_INTERVAL决定，默认为1秒。
 * 换而言之，最坏情况下会可能会出现1秒+redis调用开销时间的workerId重复，因此如果想百分百保证分布式环境下的不重复，应该在调用方做出约束，比如唯一性约束。
 *
 * @author fengwk
 */
public class RedisWorkerIdClient implements WorkerIdClient, Runnable {

    private static final Logger log = LoggerFactory.getLogger(RedisWorkerIdClient.class);

    private static final String LUA_GET_IDLE_WORKER_ID;
    private static final String LUA_KEEPALIVE_WORKER_ID;

    static {
        try {
            LUA_GET_IDLE_WORKER_ID = getLua("redis_getIdleWorkerId.lua");
            LUA_KEEPALIVE_WORKER_ID = getLua("redis_keepaliveWorkerId.lua");
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    // redis中使用的hash结构的key
    private static final String WORKER_HASH_KEY = "WORKER_HASH";
    // 锁定60秒
    private static final String LOCK_TIME = "60";
    // 保持锁定的时间间隔，1s
    private static final long KEEPALIVE_INTERVAL = 1000L;
    // 获取workerId的重试时间间隔，1s
    private static final long GET_WORKER_ID_RETRY_INTERVAL = 1000L;
    // 获取workerId的区间间隔
    private static final int GET_WORKER_ID_RETRY_RANGE_INTERVAL = 32;

    // [RANGE_MIN..RANGE_MAX)
    private static final int RANGE_FROM = 0;
    private static final int RANGE_TO = 1024;

    /**
     * 状态：尚未获取。
     */
    private static final int STATE_NOT_ACQUIRED = 1;

    /**
     * 状态：已获取。
     */
    private static final int STATE_ACQUIRED = 2;

    /**
     * 状态：关闭中。
     */
    private static final int STATE_CLOSING = 3;

    /**
     * 状态：关闭中，并释放资源。
     */
    private static final int STATE_CLOSING_AND_RELEASE_RESOURCE = 4;

    /**
     * 状态：已完成全部关闭工作。
     */
    private static final int STATE_TERMINAL = 5;

    private final ReentrantLock stateLock = new ReentrantLock();
    private final Condition acquired = stateLock.newCondition();

    private final RedisScriptExecutor scriptExecutor;
    private final String clientId = getClientId();
    private final AtomicReference<WorkerIdAndState> workerIdAndStateRef = new AtomicReference<>(
            new WorkerIdAndState(null, STATE_NOT_ACQUIRED));
    private final CountDownLatch closedCdl = new CountDownLatch(1);

    /**
     * workerId和state的快照信息。
     */
    static class WorkerIdAndState {

        final Long workerId;
        final int state;

        WorkerIdAndState(Long workerId, int state) {
            this.workerId = workerId;
            this.state = state;
        }

    }

    /**
     * 构建一个RedisWorkerIdClient，一旦构建完成RedisWorkerIdClient就成功启动了。
     *
     * @param scriptExecutor not null
     */
    public RedisWorkerIdClient(RedisScriptExecutor scriptExecutor) {
        this.scriptExecutor = Objects.requireNonNull(scriptExecutor);
        Thread runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }

    @Override
    public long get() throws ClosedException {
        // 快速路径
        WorkerIdAndState ws = workerIdAndStateRef.get();
        if (ws.state == STATE_ACQUIRED) {
            return ws.workerId;
        }

        // 等待，直到状态转为STATE_ACQUIRED
        boolean isInterrupted = false;
        stateLock.lock();
        try {
            while ((ws = workerIdAndStateRef.get()).state != STATE_ACQUIRED) {
                // 如果客户端已被关闭，直接抛出异常
                if (isClosed(ws)) {
                    throw new ClosedException("RedisWorkerIdClient '" + clientId + "' has been closed");
                }

                try {
                    acquired.await();
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
            return ws.workerId;
        } finally {
            stateLock.unlock();
            if (isInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public long getInterruptibly() throws InterruptedException, ClosedException {
        // 快速路径
        WorkerIdAndState ws = workerIdAndStateRef.get();
        if (ws.state == STATE_ACQUIRED) {
            return ws.workerId;
        }

        // 可中断地等待，尝试等到状态转为STATE_ACQUIRED
        stateLock.lockInterruptibly();
        try {
            while ((ws = workerIdAndStateRef.get()).state != STATE_ACQUIRED) {
                // 如果客户端已被关闭，直接抛出异常
                if (isClosed(ws)) {
                    throw new ClosedException("RedisWorkerIdClient '" + clientId + "' has been closed");
                }

                acquired.await();
            }
            return ws.workerId;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public Long tryGet() throws ClosedException {
        WorkerIdAndState ws = workerIdAndStateRef.get();
        // 如果客户端已被关闭，直接抛出异常
        if (isClosed(ws)) {
            throw new ClosedException("RedisWorkerIdClient '" + clientId + "' has been closed");
        }
        return ws.state == STATE_ACQUIRED ? ws.workerId : null;
    }

    @Override
    public Long tryGet(long timeout, TimeUnit unit) throws InterruptedException, ClosedException {
        // 快速路径
        WorkerIdAndState ws = workerIdAndStateRef.get();
        if (ws.state == STATE_ACQUIRED) {
            return ws.workerId;
        }

        // 有限时等待，尝试等到状态转为STATE_ACQUIRED
        long prevRecordTime = System.nanoTime();
        long remainingTime = TimeUnit.NANOSECONDS.convert(timeout, unit);
        // 尝试在指定时间内获取锁资源
        if (!stateLock.tryLock(remainingTime, TimeUnit.NANOSECONDS)) {
            return null;
        }
        try {
            while ((ws = workerIdAndStateRef.get()).state != STATE_ACQUIRED) {
                // 如果客户端已被关闭，直接抛出异常
                if (isClosed(ws)) {
                    throw new ClosedException("RedisWorkerIdClient '" + clientId + "' has been closed");
                }

                // 计算剩余时间
                long curRecordTime = System.nanoTime();
                remainingTime -= (curRecordTime - prevRecordTime);
                if (remainingTime <= 0) {
                    return null;
                }
                prevRecordTime = curRecordTime;
                // 如果还有剩余时间尝试等待状态变更
                if (!acquired.await(remainingTime, TimeUnit.NANOSECONDS)
                        && workerIdAndStateRef.get().state != STATE_ACQUIRED) {
                    // await返回false表示已超时，如果此时状态还是不对，直接返回null
                    return null;
                }
            }
            return ws.workerId;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void run() {
        boolean isInterrupted = false;
        WorkerIdAndState ws;
        while (!isTerminal(ws = workerIdAndStateRef.get())) {
            try {
                doRun(ws);
            } catch (InterruptedException e) {
                isInterrupted = true;
            } catch (Throwable err) {
                log.error("", err);
                // 释放时间片，避免不断产生异常导致的CPU 100%
                Thread.yield();
            }
        }
        if (isInterrupted) {
            Thread.currentThread().interrupt();
        }
        closedCdl.countDown();
        signalAllAcquired();
    }

    /**
     * 根据状态执行获取workerId，和workerId保活逻辑。
     *
     * @param ws not null
     * @throws Throwable
     */
    private void doRun(WorkerIdAndState ws) throws Throwable {
        switch (ws.state) {
            case STATE_NOT_ACQUIRED:
                // 分批次getIdleWorkerId，这么做虽然会减少整体吞吐量，但是可以显著减少单批处理的阻塞时间，对于大批量客户端有非常好的优化效果
                Long acquiredWorkerId = null;
                for (int i = RANGE_FROM; i < RANGE_TO; i += GET_WORKER_ID_RETRY_RANGE_INTERVAL) {
                    acquiredWorkerId = getIdleWorkerId(i, Math.min(RANGE_TO, i + GET_WORKER_ID_RETRY_RANGE_INTERVAL));
                    if (acquiredWorkerId != null) {
                        break;
                    }
                }

                if (acquiredWorkerId != null) {
                    // 获取到了workerId，那么尝试改变workerId和状态
                    if (!workerIdAndStateRef.compareAndSet(ws, new WorkerIdAndState(acquiredWorkerId, STATE_ACQUIRED))) {
                        // CAS失败则跳出重试
                        break;
                    }
                    log.info("acquired worker id '{}' succeeded", acquiredWorkerId);
                    signalAllAcquired();
                } else {
                    // 没有获取到空闲的workerId，则等待一会再重试
                    Thread.sleep(GET_WORKER_ID_RETRY_INTERVAL);
                }
                break;

            case STATE_ACQUIRED:
                // 已经获取到了workerId，那么进行续约
                Throwable err = null;
                try {
                    Long expiredTime = keepaliveWorkerId(ws.workerId);
                    if (expiredTime != null) {
                        log.debug("keepalive worker id succeeded");
                        // 续约成功，等待一会再次续约
                        Thread.sleep(KEEPALIVE_INTERVAL);
                        break;
                    }
                } catch (Throwable e) {
                    err = e;
                }

                // 如果代码执行到此处，有两种可能：
                // 1、续约保活失败
                // 2、发生异常，例如网络异常
                // 无论哪种状态，重新转到未申请到状态
                boolean cas = workerIdAndStateRef.compareAndSet(ws, new WorkerIdAndState(null, STATE_NOT_ACQUIRED));
                // 即使CAS失败，也需要继续执行下面的逻辑

                // 如果存在异常，则需抛出
                if (err != null) {
                    throw err;
                }
                // 如果CAS成功了，输出状态转移日志
                if (cas) {
                    log.warn("keepalive worker id '{}' failed, restart acquire worker id", ws.workerId);
                }
                break;

            case STATE_CLOSING:
                if (workerIdAndStateRef.compareAndSet(ws, new WorkerIdAndState(null, STATE_TERMINAL))) {
                    log.info("{} closed", RedisWorkerIdClient.class.getSimpleName());
                }
                break;

            case STATE_CLOSING_AND_RELEASE_RESOURCE:
                if (workerIdAndStateRef.compareAndSet(ws, new WorkerIdAndState(null, STATE_TERMINAL))) {
                    scriptExecutor.close();
                    log.info("{} closed and close pool", RedisWorkerIdClient.class.getSimpleName());
                }
                break;

            default:
                throw new AssertionError("unknown state '" + ws.state + "'");
        }
    }

    /**
     * 唤醒所有等待在acquired条件上的线程。
     */
    private void signalAllAcquired() {
        stateLock.lock();
        try {
            // 唤醒所有等待获取的线程
            acquired.signalAll();
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 获取客户端id。
     *
     * @return
     */
    private String getClientId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取一个[from..to)区间内空闲的workerId，并且锁定该workerId 60秒，如果不存在空闲的workerId将返回null。
     *
     * @param from 包含
     * @param to 不包含
     * @return
     */
    private Long getIdleWorkerId(int from, int to) throws Exception {
        if (from >= to) {
            return null;
        }

        return scriptExecutor.execute(
                LUA_GET_IDLE_WORKER_ID,
                Collections.singletonList(WORKER_HASH_KEY),
                Arrays.asList(clientId, LOCK_TIME, String.valueOf(from), String.valueOf(to - 1)),
                Long.class);
    }

    /**
     * 保持当前workerId的锁定，如果锁定成功返回下一次过期时间，锁定失败返回null。
     *
     * @param workerId
     * @return
     */
    private Long keepaliveWorkerId(long workerId) throws Exception {
        return scriptExecutor.execute(
                LUA_KEEPALIVE_WORKER_ID,
                Collections.singletonList(WORKER_HASH_KEY),
                Arrays.asList(clientId, LOCK_TIME, String.valueOf(workerId)),
                Long.class);
    }

    /**
     * 获取指定classpath下的LUA脚本。
     *
     * @param classpath
     * @return
     * @throws IOException
     */
    private static String getLua(String classpath) throws IOException {
        InputStream input = ClassLoader.getSystemResourceAsStream(classpath);
        if (input == null) {
            throw new AssertionError("cannot found " + classpath + ".");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = input.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        input.close();
        return out.toString(StandardCharsets.UTF_8.name());
    }

    public void close(boolean releaseResource) {
        WorkerIdAndState ws = workerIdAndStateRef.get();
        if (!isClosed(ws)) {
            WorkerIdAndState closedWs = new WorkerIdAndState(null,
                    releaseResource ? STATE_CLOSING_AND_RELEASE_RESOURCE : STATE_CLOSING);
            while (!workerIdAndStateRef.compareAndSet(ws, closedWs)) {
                ws = workerIdAndStateRef.get();
            }
        }
    }

    /**
     * 检查当前客户端是否已关闭。
     *
     * @param ws
     * @return
     */
    private boolean isClosed(WorkerIdAndState ws) {
        return ws.state >= STATE_CLOSING;
    }

    /**
     * 检查是否已经完成关闭。
     *
     * @param ws
     * @return
     */
    private boolean isTerminal(WorkerIdAndState ws) {
        return ws.state == STATE_TERMINAL;
    }

    /**
     * 阻塞，直到当前客户端调用关闭，并完成关闭。
     */
    public void waitClosed() throws InterruptedException {
        closedCdl.await();
    }

}
