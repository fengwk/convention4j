package fun.fengwk.convention4j.common.concurrent;

import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class NamedThreadFactoryTest {

    @Test
    public void test() {
        NamedThreadFactory factory = new NamedThreadFactory("test");
        Thread thread = factory.newThread(() -> {});
        assert thread.getName().equals("test-1");
    }
    
}
