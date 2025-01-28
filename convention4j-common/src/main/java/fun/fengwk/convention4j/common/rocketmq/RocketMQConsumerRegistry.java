package fun.fengwk.convention4j.common.rocketmq;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
public class RocketMQConsumerRegistry {

    private final ConcurrentMap<ConsumerGroupTopic, ListenerDefinition> listenerDefinitionRegistry = new ConcurrentHashMap<>();
    private final ConcurrentMap<ConsumerGroupTopic, BatchListenerDefinition> batchListenerDefinitionRegistry = new ConcurrentHashMap<>();

    public Set<ConsumerGroupTopic> listenerConsumerGroupTopics() {
        return listenerDefinitionRegistry.keySet();
    }

    public Set<ConsumerGroupTopic> batchListenerConsumerGroupTopics() {
        return batchListenerDefinitionRegistry.keySet();
    }

    public ListenerDefinition getListenerDefinition(ConsumerGroupTopic consumerGroupTopic) {
        return listenerDefinitionRegistry.get(consumerGroupTopic);
    }

    public BatchListenerDefinition getBatchListenerDefinition(ConsumerGroupTopic consumerGroupTopic) {
        return batchListenerDefinitionRegistry.get(consumerGroupTopic);
    }

    public void registerIfNecessary(Object bean) {
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
    private void registerConsumer(Object bean, Method method, RocketMQMessageListenerConfig listenerConfig) {
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(listenerConfig.getConsumerGroup(), listenerConfig.getTopic());
        ListenerDefinition listenerDefinition = new ListenerDefinition(bean, method, listenerConfig);
        ListenerDefinition oldDef = listenerDefinitionRegistry.putIfAbsent(consumerGroupTopic, listenerDefinition);
        if (oldDef != null) {
            throw new IllegalStateException(String.format("register consumer failed, [%s | %s] conflict occurred",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }
    }

    /**
     * 注册批量消费者
     */
    private void registerBatchConsumer(Object bean, Method method, RocketMQBatchMessageListenerConfig batchListenerConfig) {
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(batchListenerConfig.getConsumerGroup(), batchListenerConfig.getTopic());
        BatchListenerDefinition listenerDefinition = new BatchListenerDefinition(bean, method, batchListenerConfig);
        BatchListenerDefinition oldDef = batchListenerDefinitionRegistry.putIfAbsent(consumerGroupTopic, listenerDefinition);
        if (oldDef != null) {
            throw new IllegalStateException(String.format("register batch consumer failed, [%s | %s] conflict occurred",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }
    }

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
