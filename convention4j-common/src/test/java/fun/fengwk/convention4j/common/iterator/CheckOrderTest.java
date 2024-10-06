package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fengwk
 */
public class CheckOrderTest {

    @Test
    public void test1() {
        IteratorTest test = new IteratorTest(iter -> Iterators.checkOrder(iter, Order.ASC));
        test.test1();
        test.test2();
        test.test3();
    }

    @Test
    public void test2() {
        OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(-1, 1, 2, 3, 4, 5).iterator(), Order.ASC);
        while (iter.hasNext()) {
            iter.next();
        }
    }

    @Test
    public void test3() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(0, -1, 2, 3, 4, 5).iterator(), Order.ASC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test4() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(0, 1, -1, 3, 4, 5).iterator(), Order.ASC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test5() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(0, 1, 2, -1, 4, 5).iterator(), Order.ASC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test6() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(0, 1, 2, 3, -1, 5).iterator(), Order.ASC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test7() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(2, 4, 3, 2).iterator(), Order.DESC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test8() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(5, 8, 3, 2).iterator(), Order.DESC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test9() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(5, 4, 8, 2).iterator(), Order.DESC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test10() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(5, 4, 3, 8).iterator(), Order.DESC);
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    public void test11() {
        assertThrows(IllegalStateException.class, () -> {
            OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(0, -1, 2, 3, 4, 5).iterator(), Order.ASC);

            assert iter.next() == 0;
            iter.next();
        });
    }

    @Test
    public void test12() {
        OrderedIterator<Integer> iter = Iterators.checkOrder(Arrays.asList(5, 3, 3, 2).iterator(), Order.DESC);
        while (iter.hasNext()) {
            iter.next();
        }
    }

}
