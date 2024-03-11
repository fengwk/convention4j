package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.Ref;
import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class RefTest {

    @Test
    public void test1() {
        assert Ref.empty().getValue() == null;
        assert Ref.of(1).getValue() == 1;
    }
    
}
