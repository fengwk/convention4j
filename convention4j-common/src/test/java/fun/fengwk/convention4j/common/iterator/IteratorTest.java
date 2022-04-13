package fun.fengwk.convention4j.common.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class IteratorTest {

    private final Function<Iterator<Integer>, ? extends Iterator<Integer>> factory;

    public IteratorTest(Function<Iterator<Integer>, ? extends Iterator<Integer>> factory) {
        this.factory = factory;
    }

    public void test1() {
        Iterator<Integer> iter = factory.apply(Arrays.asList(0, 1, 2, 3, 4, 5).iterator());
        assert iter.hasNext();
        assert iter.next() == 0;
        assert iter.hasNext();
        assert iter.next() == 1;
        assert iter.hasNext();
        assert iter.next() == 2;
        assert iter.hasNext();
        assert iter.next() == 3;
        assert iter.hasNext();
        assert iter.next() == 4;
        assert iter.hasNext();
        assert iter.next() == 5;
        assert !iter.hasNext();
    }

    public void test2() {
        Iterator<Integer> iter = factory.apply(Collections.emptyIterator());
        assert !iter.hasNext();
    }

    public void test3() {
        Iterator<Integer> iter = factory.apply(Collections.emptyIterator());
        try {
            iter.next();
            assert false;
        } catch (NoSuchElementException e) {
            assert true;
        }
    }

}
