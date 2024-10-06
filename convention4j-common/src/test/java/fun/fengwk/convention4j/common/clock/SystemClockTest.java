package fun.fengwk.convention4j.common.clock;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author fengwk
 */
public class SystemClockTest {

    @Test
    public void test() {
        SystemClock systemClock = new SystemClock();
        long millis = systemClock.currentTimeMillis();
        long micros = systemClock.currentTimeMicros();
        assert Math.abs(micros / 1000 - millis) <= 1;
    }

}
