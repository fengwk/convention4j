package fun.fengwk.convention4j.common.clock;

/**
 * 使用系统时钟的时钟接口实现。
 *
 * @author fengwk
 */
public class SystemClock implements Clock {

    private final long millisOffset = System.currentTimeMillis();
    private final long nanoOffset = System.nanoTime();
    
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long currentTimeMicros() {
        long nanoDuration = System.nanoTime() - nanoOffset;
        long microsDuration = nanoDuration / 1000;
        return millisOffset * 1000 + microsDuration;
    }

}
