package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author fengwk
 */
public class DistinctTest {

    @Test
    public void test1() {
        IteratorTest test = new IteratorTest(iter -> Iterators.distinct(Iterators.checkOrder(iter, Order.ASC)));
        test.test1();
        test.test2();
        test.test3();
    }

    @Test
    public void test2() {
        DistinctOrderedIterator<Integer> iter = Iterators.distinct(Iterators.checkOrder(Arrays.asList(0, 2, 2, 3, 4, 5).iterator(), Order.ASC));
        assert iter.next() == 0;
        assert iter.next() == 2;
        assert iter.next() == 3;
        assert iter.next() == 4;
        assert iter.next() == 5;
        assert !iter.hasNext();
    }

    @Test
    public void test3() {
        DistinctOrderedIterator<Integer> iter = Iterators.distinct(Iterators.checkOrder(Arrays.asList(5, 5, 4, 3, 3, 2, 1, 1, 1, 1, 0).iterator(), Order.DESC));
        assert iter.next() == 5;
        assert iter.next() == 4;
        assert iter.next() == 3;
        assert iter.next() == 2;
        assert iter.next() == 1;
        assert iter.next() == 0;
        assert !iter.hasNext();
    }

}
