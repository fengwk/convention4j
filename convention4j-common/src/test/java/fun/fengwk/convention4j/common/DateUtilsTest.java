package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 
 * @author fengwk
 */
public class DateUtilsTest {

    @Test
    public void test() {
        assertEpochMilli(DateUtils.toEpochMilli(LocalDateTime.now()), System.currentTimeMillis());
        assertEpochMilli(DateUtils.toDate(LocalDateTime.now()).getTime(), System.currentTimeMillis());
        assertEpochMilli(DateUtils.toEpochMilli(DateUtils.toLocalDateTime(new Date())), System.currentTimeMillis());
    }
    
    // 这个断言并不精确，但能够处理大多数场景
    private void assertEpochMilli(long m1, long m2) {
        assert Math.abs(m1 - m2) < 1000;
    }

    @Test
    public void testConvertTimeZone() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("now: " + now);
        LocalDateTime utcNow = DateUtils.convertTimeZone(now, ZoneId.of("Asia/Shanghai"), DateUtils.zoneIdUTC());
        System.out.println("utcNow: " + utcNow);

        Duration duration = Duration.between(utcNow, now);
        Assertions.assertEquals(8, duration.toHours());
    }
    
}
