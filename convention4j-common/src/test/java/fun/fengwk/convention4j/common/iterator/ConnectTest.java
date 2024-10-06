package fun.fengwk.convention4j.common.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author fengwk
 */
public class ConnectTest {

    @Test
    public void test1() {
        Iterator<Integer> iter = Iterators.connect(Arrays.asList(Arrays.asList(1, 2).iterator(), Arrays.asList(3, 4).iterator()));
        assert iter.next() == 1;
        assert iter.next() == 2;
        assert iter.next() == 3;
        assert iter.next() == 4;
        assert !iter.hasNext();
    }

}
