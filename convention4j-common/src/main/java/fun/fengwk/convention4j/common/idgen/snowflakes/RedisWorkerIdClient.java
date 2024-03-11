package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleState;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.*;

/**
 * @author fengwk
 */
public class RedisWorkerIdClient extends AbstractLifeCycle implements WorkerIdClient {

    private static final Logger log = LoggerFactory.getLogger(RedisWorkerIdClient.class);

    /**
     * 用于申请空闲workerId的LUA脚本。
     */
    private static final String LUA_APPLY_IDLE_WORKER_ID;

    /**
     * 用于续约workerId的LUA脚本。
     */
    private static final String LUA_RENEW_WORKER_ID;

    /**
     * redis中存储所有workerId的hash结构的key。
     */
    private static final String WORKER_HASH_KEY = "REDIS_WORKER_ID_CLIENT:%s";

    /**
     * 锁过期时间，默认60秒。设置过期时间是为了防止异常情况下锁一直被占有得不到释放。
     */
    private static final String LOCK_EXPIRE = "60";

    /**
     * 续约时间间隔，1秒。
     */
    private static final long RENEW_INTERVAL = 1000L;

    /**
     * 申请错误重试时间间隔，1秒。
     */
    private static final long APPLY_ERROR_INTERVAL = 1000L;

    /**
     * 起始workerId，包含。
     */
    private static final int FROM = 0;

    /**
     * 结束workerId，不包含。
     */
    private static final int TO = 1024;

    static {
        try {
            LUA_APPLY_IDLE_WORKER_ID = getLua("redis_applyIdleWorkerId.lua");
            LUA_RENEW_WORKER_ID = getLua("redis_renewWorkerId.lua");
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    /**
     * redis脚本执行器。
     */
    private final RedisScriptExecutor scriptExecutor;

    /**
     * 当前客户端的命名空间，可以用于应用隔离。
     */
    private final String namespace;

    /**
     * 当前客户端的唯一标识。
     */
    private final String clientId = getClientId();

    /**
     * 当前维护的workerId，读取该状态需要获取workerIdRwLock中的读锁。
     */
    private Long workerId;

    /**
     * 该变量存储了与当前workerId对应的生命周期状态，从该变量中读取生命周期应该能保持与从{@link #getState()}获取的状态保持一致，
     * 另外读取该状态需要获取workerIdRwLock中的读锁。
     */
    private LifeCycleState workerIdLifeCycleState;

    /**
     * workerId对应的读写锁。
     */
    private final ReadWriteLock workerIdRwLock = new ReentrantReadWriteLock();

    /**
     * 申请到了workerId。
     */
    private final Condition appliedWorkerId = workerIdRwLock.writeLock().newCondition();

    /**
     * STARTED状态的条件
     */
    private final Condition lifeCycleChange = getLifeCycleRwLock().writeLock().newCondition();

    /**
     * 执行申请workerId任务的线程。
     */
    private final Thread applyWorkerIdThread = new Thread(new ApplyWorkerIdTask());

    /**
     * applyWorkerIdThread终止同步器。
     */
    private final CountDownLatch applyWorkerIdThreadCdl = new CountDownLatch(1);

    /**
     *
     * @param scriptExecutor not null
     */
    public RedisWorkerIdClient(String namespace, RedisScriptExecutor scriptExecutor) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.scriptExecutor = Objects.requireNonNull(scriptExecutor, "scriptExecutor cannot be null");
    }

    /**
     * 获取指定classpath下的LUA脚本。
     *
     * @param classpath
     * @return
     * @throws IOException
     */
    private static String getLua(String classpath) throws IOException {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        InputStream input = classLoader.getResourceAsStream(classpath);
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
        return out.toString(StandardCharsets.UTF_8);
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
    private Long applyIdleWorkerId(int from, int to) throws Exception {
        if (from >= to) {
            return null;
        }
        String workerHashKey = String.format(WORKER_HASH_KEY, namespace);
        log.info("Apply idle workerId from '{}' to '{}' in '{}'", from, to, workerHashKey);
        return scriptExecutor.execute(
                LUA_APPLY_IDLE_WORKER_ID,
                Collections.singletonList(workerHashKey),
                Arrays.asList(clientId, LOCK_EXPIRE, String.valueOf(from), String.valueOf(to - 1)),
                Long.class);
    }

    /**
     * 续约指定的workerId，如果续约成功返回下一次过期时间，续约失败返回null。
     *
     * @param workerId
     * @return
     */
    private Long renewWorkerId(long workerId) throws Exception {
        String workerHashKey = String.format(WORKER_HASH_KEY, namespace);
        return scriptExecutor.execute(
                LUA_RENEW_WORKER_ID,
                Collections.singletonList(workerHashKey),
                Arrays.asList(clientId, LOCK_EXPIRE, String.valueOf(workerId)),
                Long.class);
    }

    @Override
    public long get() {
        for (;;) {
            // 读锁锁定lifeCycle，使每次迭代时状态不会发生改变
            getLifeCycleRwLock().readLock().lock();
            try {
                // 如非STARTED状态抛出异常
                if (getState() != STARTED) {
                    throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                            getClass().getSimpleName(), STARTED));
                }

                // 读锁锁定workerId，使其值不会发生改变
                workerIdRwLock.readLock().lock();
                try {
                    if (workerId != null) {
                        return workerId;
                    }
                } finally {
                    workerIdRwLock.readLock().unlock();
                }

            } finally {
                getLifeCycleRwLock().readLock().unlock();
            }

            workerIdRwLock.writeLock().lock();
            try {
                while (workerId == null) {
                    if (workerIdLifeCycleState != STARTED) {
                        throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                                getClass().getSimpleName(), STARTED));
                    }

                    appliedWorkerId.awaitUninterruptibly();
                }
            } finally {
                workerIdRwLock.writeLock().unlock();
            }
        }
    }

    @Override
    public long getInterruptibly() throws InterruptedException {
        for (;;) {
            // 读锁锁定lifeCycle，使每次迭代时状态不会发生改变
            getLifeCycleRwLock().readLock().lockInterruptibly();
            try {
                // 如非STARTED状态抛出异常
                if (getState() != STARTED) {
                    throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                            getClass().getSimpleName(), STARTED));
                }

                // 读锁锁定workerId，使其值不会发生改变
                workerIdRwLock.readLock().lockInterruptibly();
                try {
                    if (workerId != null) {
                        return workerId;
                    }
                } finally {
                    workerIdRwLock.readLock().unlock();
                }

            } finally {
                getLifeCycleRwLock().readLock().unlock();
            }

            workerIdRwLock.writeLock().lockInterruptibly();
            try {
                while (workerId == null) {
                    if (workerIdLifeCycleState != STARTED) {
                        throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                                getClass().getSimpleName(), STARTED));
                    }

                    appliedWorkerId.await();
                }
            } finally {
                workerIdRwLock.writeLock().unlock();
            }
        }
    }

    @Override
    public Long tryGet() {
        // 尝试使用读锁锁定lifeCycle，使每次迭代时状态不会发生改变，
        // 如果锁定失败说明lifeCycle正在变更中不是稳定的STARTED状态，因此抛出异常
        if (!getLifeCycleRwLock().readLock().tryLock()) {
            throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                    getClass().getSimpleName(), STARTED));
        }

        try {
            // 如非STARTED状态抛出异常
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            // 尝试使用读锁锁定workerId，使其值不会发生改变，如果锁定失败，说明当前workerId正在变更中，返回null
            if (!workerIdRwLock.readLock().tryLock()) {
                return null;
            }

            try {
                return workerId;
            } finally {
                workerIdRwLock.readLock().unlock();
            }

        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    @Override
    public Long tryGet(long timeout, TimeUnit unit) throws InterruptedException {
        long timeoutNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
        long recordNanos = System.nanoTime();

        // 尝试使用读锁锁定lifeCycle，使每次迭代时状态不会发生改变，
        // 如果锁定失败说明lifeCycle正在变更中不是稳定的STARTED状态，因此抛出异常
        if (!getLifeCycleRwLock().readLock().tryLock(timeoutNanos, TimeUnit.NANOSECONDS)) {
            throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                    getClass().getSimpleName(), STARTED));
        }

        try {
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            // 更新超时时间
            timeoutNanos -= (System.nanoTime() - recordNanos);
            if (timeoutNanos <= 0) {
                return null;
            }

            // 尝试使用读锁锁定workerId，使其值不会发生改变，如果锁定失败，说明当前workerId正在变更中，返回null
            if (!workerIdRwLock.readLock().tryLock(timeoutNanos, TimeUnit.NANOSECONDS)) {
                return null;
            }

            try {
                return workerId;
            } finally {
                workerIdRwLock.readLock().unlock();
            }

        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    @Override
    protected void doInit() throws LifeCycleException {
        scriptExecutor.init();
        applyWorkerIdThread.start();
    }

    @Override
    protected void doStart() throws LifeCycleException {
        scriptExecutor.start();
    }

    @Override
    protected void doStop() throws LifeCycleException {
        scriptExecutor.stop();
    }

    @Override
    protected void doClose() throws LifeCycleException {
        scriptExecutor.close();
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void onInitializing() {
        super.onInitializing();

        setWorkerIdLifeCycleState(INITIALIZING);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onInitialized() {
        super.onInitialized();

        setWorkerIdLifeCycleState(INITIALIZED);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onStarting() {
        super.onStarting();

        setWorkerIdLifeCycleState(STARTING);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onStarted() {
        super.onStarted();

        setWorkerIdLifeCycleState(STARTED);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onStopping() {
        super.onStopping();

        setWorkerIdLifeCycleState(STOPPING);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onStopped() {
        super.onStopped();

        setWorkerIdLifeCycleState(STOPPED);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onClosing() {
        super.onClosing();

        setWorkerIdLifeCycleState(CLOSING);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onClosed() {
        super.onClosed();

        setWorkerIdLifeCycleState(CLOSED);
        signalAllLifeCycleChange();
        applyWorkerIdThread.interrupt();
        try {
            applyWorkerIdThreadCdl.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void onFailing() {
        super.onFailing();

        setWorkerIdLifeCycleState(FAILING);
        signalAllLifeCycleChange();
    }

    @Override
    protected void onFailed() {
        super.onFailed();

        setWorkerIdLifeCycleState(FAILED);
        signalAllLifeCycleChange();
        applyWorkerIdThread.interrupt();
        try {
            applyWorkerIdThreadCdl.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void setWorkerIdLifeCycleState(LifeCycleState state) {
        workerIdRwLock.writeLock().lock();
        try {
            workerIdLifeCycleState = state;
        } finally {
            workerIdRwLock.writeLock().unlock();
        }
    }

    private void signalAllLifeCycleChange() {
        getLifeCycleRwLock().writeLock().lock();
        try {
            lifeCycleChange.signalAll();
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }
    }

    class ApplyWorkerIdTask implements Runnable {

        @Override
        public void run() {
            log.info("ApplyWorkerIdTask thread start");
            try {
                Thread currentThread = Thread.currentThread();
                while (!currentThread.isInterrupted()) {
                    try {
                        if (!doRun()) {
                            log.info("ApplyWorkerIdTask thread ending");
                            break;
                        }
                    } catch (InterruptedException ignore) {
                        log.info("ApplyWorkerIdTask thread interrupted");
                        currentThread.interrupt();
                    }
                }
            } finally {
                applyWorkerIdThreadCdl.countDown();
                log.info("ApplyWorkerIdTask thread exit");
            }
        }

        private boolean doRun() throws InterruptedException {
            LifeCycleState state;

            // 尝试等待STARTED状态
            getLifeCycleRwLock().writeLock().lockInterruptibly();
            try {
                while ((state = getState()) != STARTED) {
                    resetWorkerId();

                    if (state.getCode() >= CLOSING.getCode()) {
                        return false;
                    }

                    lifeCycleChange.await();
                }
            } finally {
                getLifeCycleRwLock().writeLock().unlock();
            }

            long waitMs = 0;

            // 读锁锁定lifeCycle，使每次迭代时状态不会发生改变
            getLifeCycleRwLock().readLock().lockInterruptibly();
            try {
                if ((state = getState()).getCode() >= CLOSING.getCode()) {
                    return false;
                }

                if (state == STARTED) {
                    // 如果是STARTED状态则真正进行workerId获取何维护
                    workerIdRwLock.writeLock().lockInterruptibly();
                    try {
                        if (workerId == null) {
                            // 如果workerId为null说明要去申请workerId
                            try {
                                // 从[FROM..TO)区间中申请workerId
                                workerId = applyIdleWorkerId(FROM, TO);
                                log.info("Successfully applied for workId '{}'", workerId);
                                // 通知已申请到workerId
                                appliedWorkerId.signalAll();
                                // 如果申请workerId成功，那么等待一个续约周期后将进入续约流程
                                waitMs = RENEW_INTERVAL;
                            } catch (Exception ex) {
                                log.error("Apply workerId error", ex);
                                // 如果发生异常，需要等待一段时间后重试
                                waitMs = APPLY_ERROR_INTERVAL;
                            }
                        } else {
                            // 如果workerId存在，尝试续约
                            try {
                                if (renewWorkerId(workerId) == null) {
                                    log.warn("Renew workerId '{}' fail", workerId);
                                    // 续约失败，将workerId重置为null，这样在下次循环中会尝试重新申请workerId
                                    workerId = null;
                                } else {
                                    // 续约成功，那么等待一个续约周期后将再次进入续约流程
                                    waitMs = RENEW_INTERVAL;
                                }
                            } catch (Exception ex) {
                                log.error("Renew workerId error", ex);
                                // 发生异常，将workerId重置为null，这样在下次循环中会尝试重新申请workerId
                                workerId = null;
                            }
                        }
                    } finally {
                        workerIdRwLock.writeLock().unlock();
                    }
                } else {
                    resetWorkerId();
                }

            } finally {
                getLifeCycleRwLock().readLock().unlock();
            }

            // 如果waitMs超过0，那么等待一段时间再进入下次循环
            if (waitMs > 0) {
                Thread.sleep(waitMs);
            }

            return true;
        }

        private void resetWorkerId() throws InterruptedException {
            workerIdRwLock.writeLock().lockInterruptibly();
            try {
                workerId = null;
            } finally {
                workerIdRwLock.writeLock().unlock();
            }
        }

    }

}
