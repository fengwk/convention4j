package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.idgen.snowflakes.FixedWorkerIdClient;
import fun.fengwk.convention4j.common.idgen.snowflakes.SnowflakesIdGenerator;
import org.junit.Test;

/**
 * @author fengwk
 */
public class SimpleNamespaceIdGeneratorTest {

    @Test
    public void test() {
        SimpleNamespaceIdGenerator<Long> idGenerator = new SimpleNamespaceIdGenerator<Long>(
                ns -> new SnowflakesIdGenerator(System.currentTimeMillis(), new FixedWorkerIdClient(0L)));
        long id1 = idGenerator.next(SimpleNamespaceIdGeneratorTest.class);
        long id2 = idGenerator.next(SimpleNamespaceIdGeneratorTest.class);
        assert id2 > id1;
    }

}
