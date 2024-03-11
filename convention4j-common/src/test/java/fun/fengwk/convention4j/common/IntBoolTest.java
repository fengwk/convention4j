package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.IntBool;
import org.junit.Test;

/**
 * @author fengwk
 */
public class IntBoolTest {

    @Test
    public void test() {
        assert IntBool.bool2int(null) == null;
        assert IntBool.bool2int((Boolean) true) == IntBool.TRUE;
        assert IntBool.bool2int((Boolean) false) == IntBool.FALSE;
        assert IntBool.bool2int(true) == IntBool.TRUE;
        assert IntBool.bool2int(false) == IntBool.FALSE;

        assert IntBool.int2bool(null) == null;
        assert IntBool.int2bool((Integer) IntBool.TRUE);
        assert !IntBool.int2bool((Integer) IntBool.FALSE);
        assert IntBool.int2bool(IntBool.TRUE);
        assert !IntBool.int2bool(IntBool.FALSE);
    }

}
