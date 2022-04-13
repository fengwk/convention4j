package fun.fengwk.convention4j.common.idgen;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class FilterableIdGeneratorTest {

    @Test
    public void test() {
        List<Integer> list = Arrays.asList(0, 2, 4, 6);
        FilterableIdGenerator<Integer> idGen = 
                new FilterableIdGenerator<>(new IntegerIdGenerator(), i -> !list.contains(i));
        assert idGen.next() == 1;
        assert idGen.next() == 3;
        assert idGen.next() == 5;
        assert idGen.next() == 7;
        assert idGen.next() == 8;
    }
    
    static class IntegerIdGenerator implements IdGenerator<Integer> {

        int i;
        
        @Override
        public Integer next() {
            return i++;
        }

        @Override
        public void close(boolean releaseResource) throws Exception {
            // nothing to do
        }
    }
    
}
