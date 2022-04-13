package fun.fengwk.convention4j.common.clock;

/**
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
