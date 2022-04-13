package fun.fengwk.convention4j.common.idgen.snowflakes;

import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class SnowflakesIdGeneratorTest {

    @Test
    public void test() {
        SnowflakesIdGenerator snowflakesIdGenerator = new SnowflakesIdGenerator(System.currentTimeMillis(), new FixedWorkerIdClient(0L));
        long id1 = snowflakesIdGenerator.next();
        long id2 = snowflakesIdGenerator.next();
        assert id2 > id1;
    }
    
}
