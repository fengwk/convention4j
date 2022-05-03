package fun.fengwk.convention4j.common.idgen.uuid;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class UUIDGeneratorTest {

    @Test
    public void test() throws LifeCycleException {
        UUIDGenerator uuidGenerator = new UUIDGenerator();

        assert uuidGenerator.init();
        assert uuidGenerator.start();

        assert uuidGenerator.next().length() == 32;

        assert uuidGenerator.stop();
        assert uuidGenerator.close();
    }
    
}
