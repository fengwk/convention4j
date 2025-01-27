package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractRocketMQConsumerManager implements AutoCloseable {

    public void registerIfNecessary(Object bean) throws ClientException {
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : allDeclaredMethods) {
            RocketMQMessageListener listenerAnnotation = AnnotationUtils.findAnnotation(
                method, RocketMQMessageListener.class);
            if (listenerAnnotation != null) {
                registerConsumer(bean, method, buildRocketMQMessageListenerConfig(listenerAnnotation));
            }

            RocketMQBatchMessageListener batchListenerAnnotation = AnnotationUtils.findAnnotation(
                method, RocketMQBatchMessageListener.class);
            if (batchListenerAnnotation != null) {
                registerBatchConsumer(bean, method, buildRocketMQBatchMessageListenerConfig(batchListenerAnnotation));
            }
        }
    }

    /**
     * 注册消费者
     */
    protected abstract void registerConsumer(Object bean, Method method,
                                             RocketMQMessageListenerConfig listenerConfig) throws ClientException;

    /**
     * 注册批量消费者
     */
    protected abstract void registerBatchConsumer(Object bean, Method method,
                                                  RocketMQBatchMessageListenerConfig batchListenerConfig) throws ClientException;


    /**
     * 刷新消费者，需要start之后才能成功刷新
     */
    public abstract boolean refreshConsumer(String consumerGroup, String topic) throws ClientException;

    /**
     * 刷新批量消费者，需要start之后才能成功刷新
     */
    public abstract boolean refreshBatchConsumer(String consumerGroup, String topic) throws ClientException;

    /**
     * 启动所有消费者
     */
    public abstract void start() throws ClientException;

    private RocketMQMessageListenerConfig buildRocketMQMessageListenerConfig(
        RocketMQMessageListener ann) {
        RocketMQMessageListenerConfig config = new RocketMQMessageListenerConfig();
        config.setConsumerGroup(ann.consumerGroup());
        config.setTopic(ann.topic());
        config.setFilterExpressionType(ann.filterExpressionType());
        config.setFilterExpression(ann.filterExpression());
        config.setMaxCacheMessageCount(ann.maxCacheMessageCount());
        config.setMaxCacheMessageSizeInBytes(ann.maxCacheMessageSizeInBytes());
        config.setConsumptionThreadCount(ann.consumptionThreadCount());
        return config;
    }

    private RocketMQBatchMessageListenerConfig buildRocketMQBatchMessageListenerConfig(
        RocketMQBatchMessageListener ann) {
        RocketMQBatchMessageListenerConfig config = new RocketMQBatchMessageListenerConfig();
        config.setConsumerGroup(ann.consumerGroup());
        config.setTopic(ann.topic());
        config.setFilterExpressionType(ann.filterExpressionType());
        config.setFilterExpression(ann.filterExpression());
        config.setMaxMessageNum(ann.maxMessageNum());
        config.setInvisibleDurationMs(ann.invisibleDurationMs());
        config.setConsumptionThreadCount(ann.consumptionThreadCount());
        return config;
    }

}
