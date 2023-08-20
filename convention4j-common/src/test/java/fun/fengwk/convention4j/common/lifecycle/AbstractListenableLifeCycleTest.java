package fun.fengwk.convention4j.common.lifecycle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
public class AbstractListenableLifeCycleTest {

    static class TestDoState {

        boolean doInit;
        boolean doStart;
        boolean doStop;
        boolean doClose;
        boolean doFail;

    }

    static class TestListenableLifeCycle extends AbstractListenableLifeCycle {

        private final TestDoState testStateSet;

        public TestListenableLifeCycle(TestDoState testStateSet) {
            this(testStateSet, Collections.emptyList());
        }

        public TestListenableLifeCycle(TestDoState testStateSet, Collection<LifeCycleListener> listeners) {
            super(listeners);
            this.testStateSet = testStateSet;
        }

        @Override
        protected void doInit() throws LifeCycleException {
            testStateSet.doInit = true;
        }

        @Override
        protected void doStart() throws LifeCycleException {
            testStateSet.doStart = true;
        }

        @Override
        protected void doStop() throws LifeCycleException {
            testStateSet.doStop = true;
        }

        @Override
        protected void doClose() throws LifeCycleException {
            testStateSet.doClose = true;
        }

        @Override
        protected void doFail() throws LifeCycleException {
            testStateSet.doFail = true;
        }
    }

    @Test
    public void test1() throws LifeCycleException {
        TestDoState testDoState = new TestDoState();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(testDoState);

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        assert lifeCycle.close();
        assert lifeCycle.getState() == LifeCycleState.CLOSED;

        assert testDoState.doInit;
        assert testDoState.doStart;
        assert testDoState.doStop;
        assert testDoState.doClose;
        assert !testDoState.doFail;
    }

    @Test
    public void test2() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert !lifeCycle.start();
        assert !lifeCycle.stop();
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
    }

    @Test
    public void test3() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert !lifeCycle.start();
        assert !lifeCycle.stop();
        assert lifeCycle.close();
        assert lifeCycle.getState() == LifeCycleState.CLOSED;
    }

    @Test
    public void test4() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert !lifeCycle.init();
        assert !lifeCycle.stop();
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
    }

    @Test
    public void test5() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert !lifeCycle.init();
        assert !lifeCycle.stop();
        assert lifeCycle.close();
        assert lifeCycle.getState() == LifeCycleState.CLOSED;
    }

    @Test
    public void test6() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert !lifeCycle.init();
        assert !lifeCycle.start();
        assert !lifeCycle.close();
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
    }

    @Test
    public void test7() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        assert !lifeCycle.init();
        assert !lifeCycle.stop();
        assert lifeCycle.close();
        assert lifeCycle.getState() == LifeCycleState.CLOSED;
    }

    @Test
    public void test8() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        assert !lifeCycle.init();
        assert !lifeCycle.stop();
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
    }

    @Test
    public void test9() {
        TestDoState testDoState = new TestDoState();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(testDoState) {
            @Override
            protected void doInit() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        assert lifeCycle.getState() == LifeCycleState.NEW;
        try {
            lifeCycle.init();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert testDoState.doFail;
    }

    @Test
    public void test10() throws LifeCycleException {
        TestDoState testDoState = new TestDoState();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(testDoState) {
            @Override
            protected void doStart() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        try {
            lifeCycle.start();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert testDoState.doFail;
    }

    @Test
    public void test11() throws LifeCycleException {
        TestDoState testDoState = new TestDoState();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(testDoState) {
            @Override
            protected void doStop() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        try {
            lifeCycle.stop();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert testDoState.doFail;
    }

    @Test
    public void test12() throws LifeCycleException {
        TestDoState testDoState = new TestDoState();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(testDoState) {
            @Override
            protected void doClose() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        try {
            lifeCycle.close();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert testDoState.doFail;
    }

    @Test
    public void test13() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState());

        List<LifeCycleState> list = new ArrayList<>();
        lifeCycle.addLifeCycleListener(list::add);

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        assert lifeCycle.close();
        assert lifeCycle.getState() == LifeCycleState.CLOSED;

        assert list.get(0) == LifeCycleState.INITIALIZING;
        assert list.get(1) == LifeCycleState.INITIALIZED;
        assert list.get(2) == LifeCycleState.STARTING;
        assert list.get(3) == LifeCycleState.STARTED;
        assert list.get(4) == LifeCycleState.STOPPING;
        assert list.get(5) == LifeCycleState.STOPPED;
        assert list.get(6) == LifeCycleState.CLOSING;
        assert list.get(7) == LifeCycleState.CLOSED;
    }

    @Test
    public void test14() throws LifeCycleException {
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState()) {
            @Override
            protected void doClose() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        List<LifeCycleState> list = new ArrayList<>();
        lifeCycle.addLifeCycleListener(list::add);

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        try {
            lifeCycle.close();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert list.get(0) == LifeCycleState.INITIALIZING;
        assert list.get(1) == LifeCycleState.INITIALIZED;
        assert list.get(2) == LifeCycleState.STARTING;
        assert list.get(3) == LifeCycleState.STARTED;
        assert list.get(4) == LifeCycleState.STOPPING;
        assert list.get(5) == LifeCycleState.STOPPED;
        assert list.get(6) == LifeCycleState.CLOSING;
        assert list.get(7) == LifeCycleState.FAILING;
        assert list.get(8) == LifeCycleState.FAILED;
    }

    @Test
    public void test15() throws LifeCycleException {
        List<LifeCycleState> list = new ArrayList<>();
        TestListenableLifeCycle lifeCycle = new TestListenableLifeCycle(new TestDoState(), Collections.singletonList(list::add)) {
            @Override
            protected void doClose() throws LifeCycleException {
                throw new LifeCycleException();
            }
        };

        assert lifeCycle.getState() == LifeCycleState.NEW;
        assert lifeCycle.init();
        assert lifeCycle.getState() == LifeCycleState.INITIALIZED;
        assert lifeCycle.start();
        assert lifeCycle.getState() == LifeCycleState.STARTED;
        assert lifeCycle.stop();
        assert lifeCycle.getState() == LifeCycleState.STOPPED;
        try {
            lifeCycle.close();
        } catch (LifeCycleException ex) {
            assert lifeCycle.getState() == LifeCycleState.FAILED;
        }

        assert list.get(0) == LifeCycleState.NEW;
        assert list.get(1) == LifeCycleState.INITIALIZING;
        assert list.get(2) == LifeCycleState.INITIALIZED;
        assert list.get(3) == LifeCycleState.STARTING;
        assert list.get(4) == LifeCycleState.STARTED;
        assert list.get(5) == LifeCycleState.STOPPING;
        assert list.get(6) == LifeCycleState.STOPPED;
        assert list.get(7) == LifeCycleState.CLOSING;
        assert list.get(8) == LifeCycleState.FAILING;
        assert list.get(9) == LifeCycleState.FAILED;
    }
    
}
