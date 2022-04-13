package fun.fengwk.convention4j.common.iterator;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author fengwk
 */
public class CheckNotNullElementTest {

    @Test
    public void test1() {
        IteratorTest test = new IteratorTest(Iterators::checkNotNullElement);
        test.test1();
        test.test2();
        test.test3();
    }

    @Test(expected = IllegalStateException.class)
    public void test2() {
        Iterator<Integer> iter = Iterators.checkNotNullElement(Arrays.asList(0, 1, 2, null, 3, 4, 5).iterator());
        while (iter.hasNext()) {
            iter.next();
        }
    }

}
