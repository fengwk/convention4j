package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.Subscribe;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Data
@Component
public class MyListener implements EventListener {

    private Integer integerEvent;
    private MyEvent myEvent;


    @Subscribe
    public void onIntegerEvent(Integer integerEvent) {
        this.integerEvent = integerEvent;
    }

    @Subscribe
    public void onMyEvent(MyEvent myEvent) {
        this.myEvent = myEvent;
    }

}
