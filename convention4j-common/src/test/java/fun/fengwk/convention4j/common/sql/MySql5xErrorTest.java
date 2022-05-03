package fun.fengwk.convention4j.common.sql;

import org.junit.Test;

/**
 * @author fengwk
 */
public class MySql5xErrorTest {

    @Test
    public void test1() {
        String entry = MySql5xError.parseDuplicateEntry("Duplicate entry '1234567890' for key 'uk_mobile'");
        assert entry.equals("1234567890");
    }

    @Test
    public void test2() {
        String key = MySql5xError.parseDuplicateKey("Duplicate entry '1234567890' for key 'uk_mobile'");
        assert key.equals("uk_mobile");
    }

}
