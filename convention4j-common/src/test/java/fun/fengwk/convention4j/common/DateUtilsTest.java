package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    
}
