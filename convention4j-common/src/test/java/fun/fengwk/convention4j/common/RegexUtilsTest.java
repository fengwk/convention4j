package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.RegexUtils;
import org.junit.Test;

/**
 * @author fengwk
 */
public class RegexUtilsTest {

    @Test
    public void testEmail() {
        assert RegexUtils.isEmail("759543714@qq.com");
        assert RegexUtils.isEmail("xxxxxxxxxx@123.xxx.com");
        assert RegexUtils.isEmail("lw38373238dangji@163.com");
        assert RegexUtils.isEmail("oll6907573taoy@163.com");
        assert RegexUtils.isEmail("skxs55899248che@163.com");
        assert !RegexUtils.isEmail("123123123");
        assert !RegexUtils.isEmail("fengwkfengwk");
        assert !RegexUtils.isEmail("123fengwk");
    }

    @Test
    public void testChineseMobile() {
        assert RegexUtils.isChineseMobile("15128159999");
        assert RegexUtils.isChineseMobile("18695097743");
        assert RegexUtils.isChineseMobile("18218506929");
        assert RegexUtils.isChineseMobile("18218506929");
        assert RegexUtils.isChineseMobile("15706883672");
        assert !RegexUtils.isChineseMobile("skxs55899248che@163.com");
        assert !RegexUtils.isChineseMobile("157068836722");
    }

}
