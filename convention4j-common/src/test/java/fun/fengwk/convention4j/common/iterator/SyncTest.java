package fun.fengwk.convention4j.common.iterator;

import org.junit.Test;

/**
 * @author fengwk
 */
public class SyncTest {

    @Test
    public void test1() {
        IteratorTest test = new IteratorTest(Iterators::sync);
        test.test1();
        test.test2();
        test.test3();
    }

}
