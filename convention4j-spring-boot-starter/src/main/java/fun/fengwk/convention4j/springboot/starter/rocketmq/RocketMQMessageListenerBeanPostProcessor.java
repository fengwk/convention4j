package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author fengwk
 */
public class RocketMQMessageListenerBeanPostProcessor implements BeanPostProcessor {

    private final AbstractRocketMQConsumerManager rocketMQConsumerManager;

    public RocketMQMessageListenerBeanPostProcessor(AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        this.rocketMQConsumerManager = rocketMQConsumerManager;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            rocketMQConsumerManager.registerIfNecessary(bean);
        } catch (ClientException ex) {
            throw new BeanInitializationException("can not init consumer", ex);
        }
        return bean;
    }

}
