package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.EventBus;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class EventBusTest {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private MyListener myListener;

    @Test
    public void test() {
        eventBus.post(123);

        MyEvent myEvent = new MyEvent();
        myEvent.setName("fengwk");
        eventBus.post(myEvent);

        assert myListener.getIntegerEvent().equals(123);
        assert myListener.getMyEvent().equals(myEvent);
    }

}
