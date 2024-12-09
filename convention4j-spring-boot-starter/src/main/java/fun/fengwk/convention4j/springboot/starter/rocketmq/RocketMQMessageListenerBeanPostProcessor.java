package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author fengwk
 */
public class RocketMQMessageListenerBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            rocketMQConsumerManager.start();
        } catch (ClientException ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

}
