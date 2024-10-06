package fun.fengwk.convention4j.common.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void test2() {
        assertThrows(IllegalStateException.class, () -> {
            Iterator<Integer> iter = Iterators.checkNotNullElement(Arrays.asList(0, 1, 2, null, 3, 4, 5).iterator());
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

}
