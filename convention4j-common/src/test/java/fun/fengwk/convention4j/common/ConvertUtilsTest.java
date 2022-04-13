package fun.fengwk.convention4j.common;

import org.junit.Test;

/**
 * @author fengwk
 */
public class ConvertUtilsTest {

    @Test
    public void test() {
        assert ConvertUtils.bool2int(null) == null;
        assert ConvertUtils.bool2int((Boolean) true) == ConvertUtils.INT_TRUE;
        assert ConvertUtils.bool2int((Boolean) false) == ConvertUtils.INT_FALSE;
        assert ConvertUtils.bool2int(true) == ConvertUtils.INT_TRUE;
        assert ConvertUtils.bool2int(false) == ConvertUtils.INT_FALSE;

        assert ConvertUtils.int2bool(null) == null;
        assert ConvertUtils.int2bool((Integer) ConvertUtils.INT_TRUE);
        assert !ConvertUtils.int2bool((Integer) ConvertUtils.INT_FALSE);
        assert ConvertUtils.int2bool(ConvertUtils.INT_TRUE);
        assert !ConvertUtils.int2bool(ConvertUtils.INT_FALSE);
    }

}
