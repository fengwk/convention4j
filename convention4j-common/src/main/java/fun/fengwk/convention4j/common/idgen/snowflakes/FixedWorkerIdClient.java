package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

import java.util.concurrent.TimeUnit;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;

/**
 * 固定workerId的获取器。
 *
 * @author fengwk
 */
public class FixedWorkerIdClient extends AbstractLifeCycle implements WorkerIdClient {

    private final long workerId;

    public FixedWorkerIdClient(long workerId) {
        if (workerId < 0 || workerId >= 1024) {
            throw new IllegalArgumentException("range of worker id is [0..1024).");
        }

        this.workerId = workerId;
    }

    @Override
    public long get() {
        return doGet();
    }

    @Override
    public long getInterruptibly() {
        return doGet();
    }

    @Override
    public Long tryGet() {
        return doGet();
    }

    @Override
    public Long tryGet(long timeout, TimeUnit unit) {
        return doGet();
    }

    private long doGet() {
        getLifeCycleRwLock().readLock().lock();
        try {
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            return workerId;
        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    @Override
    protected void doInit() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStart() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStop() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doClose() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

}
