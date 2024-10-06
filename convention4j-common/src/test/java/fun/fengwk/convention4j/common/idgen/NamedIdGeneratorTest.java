package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.idgen.snowflakes.FixedWorkerIdClient;
import fun.fengwk.convention4j.common.idgen.snowflakes.SnowflakesIdGenerator;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author fengwk
 */
public class NamedIdGeneratorTest {

    @Test
    public void test() throws LifeCycleException {
        NamedIdGenerator<Long> namedIdGenerator = new NamedIdGenerator<>(
                new SnowflakesIdGenerator(System.currentTimeMillis(), new FixedWorkerIdClient(0L)), "myName");

        assert namedIdGenerator.init();
        assert namedIdGenerator.start();

        long id1 = namedIdGenerator.next();
        long id2 = namedIdGenerator.next();
        assert id2 > id1;
        
        assert namedIdGenerator.toString().equals("myName");

        assert namedIdGenerator.stop();
        assert namedIdGenerator.close();
    }
    
}
