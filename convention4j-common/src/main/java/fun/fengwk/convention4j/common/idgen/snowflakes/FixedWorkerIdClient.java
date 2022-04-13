package fun.fengwk.convention4j.common.idgen.snowflakes;

import java.util.concurrent.TimeUnit;

/**
 * 固定workerId的获取器。
 *
 * @author fengwk
 */
public class FixedWorkerIdClient implements WorkerIdClient {

    private final long workerId;

    public FixedWorkerIdClient(long workerId) {
        if (workerId < 0 || workerId >= 1024) {
            throw new IllegalArgumentException("range of worker id is [0..1024).");
        }

        this.workerId = workerId;
    }

    @Override
    public long get() {
        return workerId;
    }

    @Override
    public long getInterruptibly() throws InterruptedException {
        return workerId;
    }

    @Override
    public Long tryGet() {
        return workerId;
    }

    @Override
    public Long tryGet(long timeout, TimeUnit unit) throws InterruptedException {
        return workerId;
    }

    @Override
    public void close(boolean releaseResource) throws Exception {
        // nothing to do
    }

}
