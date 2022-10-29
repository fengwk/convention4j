package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * {@link EventBus}自动装配。
 *
 * @author fengwk
 */
@ConditionalOnClass(EventBus.class)
@Configuration
public class EventBusAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EventBusAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public DeadEventListener deadEventListener() {
        return new DefaultDeadEventListener();
    }

    @Bean
    public EventBus eventBus(List<EventListener> eventListeners, DeadEventListener deadEventListener) {
        EventBus eventBus = new EventBus();
        // 集成监听器
        for (EventListener eventListener : eventListeners) {
            eventBus.register(eventListener);
        }
        // 集成死信监听器
        eventBus.register(deadEventListener);
        log.info("{} autoconfiguration successfully, eventListeners: {}, deadEventListener: {}",
                EventBus.class.getSimpleName(), eventListeners, deadEventListener);
        return eventBus;
    }

}
