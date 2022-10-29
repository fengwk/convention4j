package fun.fengwk.convention4j.common.sql;

import org.junit.Test;

/**
 * @author fengwk
 */
public class MySql5xErrorTest {

    @Test
    public void test() {
        DuplicateErrorInfo errorInfo = MySql5xError.parseDuplicateErrorInfo("Duplicate entry '1234567890' for key 'uk_mobile'");
        assert errorInfo != null;
        assert errorInfo.getKey().equals("uk_mobile");
        assert errorInfo.getEntry().equals("1234567890");
    }

}
