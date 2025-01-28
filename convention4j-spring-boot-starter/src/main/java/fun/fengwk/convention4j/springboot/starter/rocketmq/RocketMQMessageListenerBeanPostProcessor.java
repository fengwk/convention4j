package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

/**
 *
 * @author fengwk
 */
public class RocketMQMessageListenerBeanPostProcessor implements BeanPostProcessor {

    private final RocketMQConsumerRegistry rocketMQConsumerRegistry;

    public RocketMQMessageListenerBeanPostProcessor(RocketMQConsumerRegistry rocketMQConsumerRegistry) {
        this.rocketMQConsumerRegistry = Objects.requireNonNull(rocketMQConsumerRegistry);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        rocketMQConsumerRegistry.registerIfNecessary(bean);
        return bean;
    }

}
