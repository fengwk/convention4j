package fun.fengwk.convention4j.common.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author fengwk
 */
public class MapTest {

    @Test
    public void test1() {
        Iterator<Integer> iter = Iterators.map(Arrays.asList(1, 2, 3).iterator(), i -> i + 1);
        assert iter.next() == 2;
        assert iter.next() == 3;
        assert iter.next() == 4;
        assert !iter.hasNext();
    }

}
