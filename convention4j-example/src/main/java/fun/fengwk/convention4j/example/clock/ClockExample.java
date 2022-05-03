package fun.fengwk.convention4j.example.clock;

import fun.fengwk.convention4j.common.clock.Clock;
import fun.fengwk.convention4j.common.clock.SystemClock;

/**
 * @author fengwk
 */
public class ClockExample {

    public static void main(String[] args) {
        Clock clock = new SystemClock();
        System.out.println("currentTimeMillis: " + clock.currentTimeMillis());
        System.out.println("currentTimeMicros: " + clock.currentTimeMicros());
    }

}
