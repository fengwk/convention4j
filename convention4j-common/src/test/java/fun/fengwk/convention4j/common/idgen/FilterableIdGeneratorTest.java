package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class FilterableIdGeneratorTest {

    @Test
    public void test() throws LifeCycleException {
        List<Integer> list = Arrays.asList(0, 2, 4, 6);
        FilterableIdGenerator<Integer> idGen = 
                new FilterableIdGenerator<>(new IntegerIdGenerator(), i -> !list.contains(i));

        assert idGen.init();
        assert idGen.start();

        assert idGen.next() == 1;
        assert idGen.next() == 3;
        assert idGen.next() == 5;
        assert idGen.next() == 7;
        assert idGen.next() == 8;

        assert idGen.stop();
        assert idGen.close();
    }
    
    static class IntegerIdGenerator extends AbstractIdGenerator<Integer> {

        int i;

        protected IntegerIdGenerator() throws LifeCycleException {
            super();
        }

        @Override
        protected Integer doNext() {
            return i++;
        }

        @Override
        protected void doInit() throws LifeCycleException {
            // nothing to do
        }

        @Override
        protected void doStart() throws LifeCycleException {
            // nothing to do
        }

        @Override
        protected void doStop() throws LifeCycleException {
            // nothing to do
        }

        @Override
        protected void doClose() throws LifeCycleException {
            // nothing to do
        }

        @Override
        protected void doFail() throws LifeCycleException {
            // nothing to do
        }

    }
    
}
