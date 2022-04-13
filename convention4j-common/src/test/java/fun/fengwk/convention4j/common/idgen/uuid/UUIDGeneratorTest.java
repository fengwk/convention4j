package fun.fengwk.convention4j.common.idgen.uuid;

import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class UUIDGeneratorTest {

    @Test
    public void test() {
        UUIDGenerator uuidGenerator = new UUIDGenerator();
        assert uuidGenerator.next().length() == 32;
    }
    
}
