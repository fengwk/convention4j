package fun.fengwk.convention4j.common.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author fengwk
 */
public class AppendTest {

    @Test
    public void test1() {
        Iterator<Integer> iter = Iterators.append(Arrays.asList(1, 2).iterator(), 3);
        assert iter.next() == 1;
        assert iter.next() == 2;
        assert iter.next() == 3;
        assert !iter.hasNext();
    }

}
