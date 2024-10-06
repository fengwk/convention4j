package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fengwk
 */
public class UnionTest {

    @Test
    public void test1() {
        DistinctOrderedIterator<Integer> iter1 = Iterators.distinct(Iterators.checkOrder(Collections.<Integer>emptyIterator(), Order.ASC));
        DistinctOrderedIterator<Integer> iter2 = Iterators.distinct(Iterators.checkOrder(Collections.<Integer>emptyIterator(), Order.ASC));
        DistinctOrderedIterator<Integer> unionIter = Iterators.union(Arrays.asList(iter1, iter2));
        assert !unionIter.hasNext();
    }

    @Test
    public void test2() {
        assertThrows(IllegalArgumentException.class, () -> {
            DistinctOrderedIterator<Integer> iter1 = Iterators.distinct(Iterators.checkOrder(Collections.<Integer>emptyIterator(), Order.ASC));
            DistinctOrderedIterator<Integer> iter2 = Iterators.distinct(Iterators.checkOrder(Collections.<Integer>emptyIterator(), Order.DESC));
            Iterators.union(Arrays.asList(iter1, iter2));
        });
    }

    @Test
    public void test3() {
        DistinctOrderedIterator<Integer> iter1 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(1, 2, 3, 5, 7, 8).iterator(), Order.ASC));
        DistinctOrderedIterator<Integer> iter2 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(-1, 1, 2, 3, 8, 11, 13).iterator(), Order.ASC));
        DistinctOrderedIterator<Integer> iter3 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(2, 4, 7, 8, 9).iterator(), Order.ASC));
        DistinctOrderedIterator<Integer> iter4 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(2, 3, 5, 8, 9, 10).iterator(), Order.ASC));
        DistinctOrderedIterator<Integer> unionIter = Iterators.union(Arrays.asList(iter1, iter2, iter3, iter4));
        assert unionIter.next() == 2;
        assert unionIter.next() == 8;
        assert !unionIter.hasNext();
    }

    @Test
    public void test4() {
        DistinctOrderedIterator<Integer> iter1 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(8, 7, 5, 3, 2, 1).iterator(), Order.DESC));
        DistinctOrderedIterator<Integer> iter2 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(13, 11, 8, 3, 2, 1, -1).iterator(), Order.DESC));
        DistinctOrderedIterator<Integer> iter3 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(9, 8, 7, 4, 2).iterator(), Order.DESC));
        DistinctOrderedIterator<Integer> iter4 = Iterators.distinct(Iterators.checkOrder(Arrays.asList(10, 9, 8, 5, 3, 2).iterator(), Order.DESC));
        DistinctOrderedIterator<Integer> unionIter = Iterators.union(Arrays.asList(iter1, iter2, iter3, iter4));
        assert unionIter.next() == 8;
        assert unionIter.next() == 2;
        assert !unionIter.hasNext();
    }

}
