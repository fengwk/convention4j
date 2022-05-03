package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
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
    public EventBus eventBus(DeadEventListener deadEventListener) {
        EventBus eventBus = new EventBus();
        eventBus.register(deadEventListener);
        log.info("{} autoconfiguration successfully, deadEventListener: {}",
                EventBus.class.getSimpleName(), deadEventListener);
        return eventBus;
    }

}
