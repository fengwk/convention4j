package fun.fengwk.convention.springboot.starter.eventbus;

import com.google.common.eventbus.EventBus;
import fun.fengwk.convention.springboot.starter.gson.GsonAutoConfiguration;
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

    private static final Logger LOG = LoggerFactory.getLogger(EventBusAutoConfiguration.class);

    @ConditionalOnMissingBean(DeadEventListener.class)
    @Bean
    public DeadEventListener deadEventListener() {
        return new DefaultDeadEventListener();
    }

    @Bean
    public EventBus eventBus(DeadEventListener deadEventListener) {
        EventBus eventBus = new EventBus();
        eventBus.register(deadEventListener);
        LOG.info("{} autoconfiguration successfully, deadEventListener: {}",
                EventBusAutoConfiguration.class.getSimpleName(), deadEventListener);
        return eventBus;
    }

}
