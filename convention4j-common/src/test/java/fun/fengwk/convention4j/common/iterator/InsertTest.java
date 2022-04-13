package fun.fengwk.convention4j.common.iterator;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author fengwk
 */
public class InsertTest {

    @Test
    public void test1() {
        Iterator<Integer> iter = Iterators.insert(Arrays.asList(1, 2).iterator(), 0);
        assert iter.next() == 0;
        assert iter.next() == 1;
        assert iter.next() == 2;
        assert !iter.hasNext();
    }

}
