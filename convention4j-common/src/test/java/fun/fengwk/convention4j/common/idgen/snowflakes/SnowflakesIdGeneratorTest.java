package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class SnowflakesIdGeneratorTest {

    @Test
    public void test() throws LifeCycleException {
        SnowflakesIdGenerator snowflakesIdGenerator = new SnowflakesIdGenerator(
                System.currentTimeMillis(), new FixedWorkerIdClient(0L));

        snowflakesIdGenerator.init();
        snowflakesIdGenerator.start();

        long id1 = snowflakesIdGenerator.next();
        long id2 = snowflakesIdGenerator.next();
        assert id2 > id1;

        snowflakesIdGenerator.stop();
        snowflakesIdGenerator.close();
    }
    
}
