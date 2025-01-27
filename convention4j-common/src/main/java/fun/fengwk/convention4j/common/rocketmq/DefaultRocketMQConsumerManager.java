package fun.fengwk.convention4j.common.rocketmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
public class DefaultRocketMQConsumerManager extends AbstractRocketMQConsumerManager {

    private final ClientConfiguration clientConfiguration;
    private final RocketMQConsumerManagerProcessor rocketMQConsumerManagerProcessor;

    private final ConcurrentMap<ConsumerGroupTopic, PushConsumerDefinition> builderRegistry = new ConcurrentHashMap<>();
    private final ConcurrentMap<ConsumerGroupTopic, PushConsumer> consumerRegistry = new ConcurrentHashMap<>();

    private final ConcurrentMap<ConsumerGroupTopic, BatchConsumerDefinition> batchConsumerRegistry = new ConcurrentHashMap<>();

    private boolean started;

    public DefaultRocketMQConsumerManager(ClientConfiguration clientConfiguration,
                                          RocketMQConsumerManagerProcessor rocketMQConsumerManagerProcessor) {
        this.clientConfiguration = Objects.requireNonNull(clientConfiguration);
        this.rocketMQConsumerManagerProcessor = rocketMQConsumerManagerProcessor;
    }

    @Override
    protected synchronized void registerConsumer(Object bean, Method method, RocketMQMessageListenerConfig listenerConfig) {
        RocketMQMessageListenerConfig originalListenerConfig = copy(listenerConfig);
        PushConsumerBuilder pushConsumerBuilder = buildPushConsumerBuilder(bean, method, listenerConfig);
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(
            listenerConfig.getConsumerGroup(), listenerConfig.getTopic());
        PushConsumerDefinition definition = new PushConsumerDefinition(
            bean, method, originalListenerConfig, pushConsumerBuilder);
        builderRegistry.put(consumerGroupTopic, definition);
    }

    @Override
    protected synchronized void registerBatchConsumer(Object bean, Method method, RocketMQBatchMessageListenerConfig listenerConfig) {
        RocketMQBatchMessageListenerConfig originalListenerConfig = copy(listenerConfig);
        RocketMQBatchMessageListenerContainer batchListenerContainer = buildBatchMessageListenerContainer(
            bean, method, listenerConfig);
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(
            listenerConfig.getConsumerGroup(), listenerConfig.getTopic());
        BatchConsumerDefinition definition = new BatchConsumerDefinition(
            bean, method, originalListenerConfig, batchListenerContainer);
        batchConsumerRegistry.put(consumerGroupTopic, definition);
    }

    @Override
    public synchronized boolean refreshConsumer(String consumerGroup, String topic) throws ClientException {
        if (!started) {
            return false;
        }

        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(consumerGroup, topic);
        PushConsumerDefinition definition = builderRegistry.get(consumerGroupTopic);
        if (definition == null) {
            return false;
        }

        PushConsumerBuilder newPushConsumerBuilder = buildPushConsumerBuilder(
            definition.getBean(), definition.getMethod(), definition.getListenerConfig());
        PushConsumer pushConsumer = consumerRegistry.get(consumerGroupTopic);
        try {
            pushConsumer.close();
        } catch (IOException ex) {
            log.error("close push consumer error, consumerGroupTopic: {}", consumerGroupTopic, ex);
        }

        definition.setPushConsumerBuilder(newPushConsumerBuilder);
        PushConsumer newPushConsumer = newPushConsumerBuilder.build(clientConfiguration);
        consumerRegistry.put(consumerGroupTopic, newPushConsumer);
        return true;
    }

    @Override
    public synchronized boolean refreshBatchConsumer(String consumerGroup, String topic) throws ClientException {
        if (!started) {
            return false;
        }

        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(consumerGroup, topic);
        BatchConsumerDefinition definition = batchConsumerRegistry.get(consumerGroupTopic);
        if (definition == null) {
            return false;
        }

        RocketMQBatchMessageListenerContainer newBatchListenerContainer = buildBatchMessageListenerContainer(
            definition.getBean(), definition.getMethod(), definition.getListenerConfig());

        RocketMQBatchMessageListenerContainer batchListenerContainer = definition.getBatchListenerContainer();
        try {
            batchListenerContainer.close();
        } catch (IOException ex) {
            log.error("close batch message listener container error, consumerGroupTopic: {}", consumerGroupTopic, ex);
        }

        definition.setBatchListenerContainer(newBatchListenerContainer);

        return true;
    }

    @Override
    public synchronized void start() throws ClientException {
        for (Map.Entry<ConsumerGroupTopic, PushConsumerDefinition> entry : builderRegistry.entrySet()) {
            PushConsumer pushConsumer = entry.getValue().getPushConsumerBuilder().build(clientConfiguration);
            consumerRegistry.put(entry.getKey(), pushConsumer);
        }

        for (BatchConsumerDefinition batchConsumerDefinition : batchConsumerRegistry.values()) {
            batchConsumerDefinition.getBatchListenerContainer().start(clientConfiguration);
        }

        this.started = true;
    }

    @Override
    public synchronized void close() {
        for (PushConsumer consumer : consumerRegistry.values()) {
            try {
                consumer.close();
            } catch (IOException ex) {
                log.error("close consumer error, consumer: {}", consumer, ex);
            }
        }

        for (BatchConsumerDefinition batchConsumerDefinition : batchConsumerRegistry.values()) {
            RocketMQBatchMessageListenerContainer container = batchConsumerDefinition.getBatchListenerContainer();
            try {
                container.close();
            } catch (IOException ex) {
                log.error("close batch container error, container: {}", container, ex);
            }
        }
    }

    private PushConsumerBuilder buildPushConsumerBuilder(Object bean, Method method, RocketMQMessageListenerConfig listenerConfig) {
        if (rocketMQConsumerManagerProcessor != null) {
            rocketMQConsumerManagerProcessor.initialize(listenerConfig);
        }
        PushConsumerBuilder pushConsumerBuilder = new PushConsumerBuilder();
        pushConsumerBuilder.setConsumerGroup(listenerConfig.getConsumerGroup());
        pushConsumerBuilder.setListener(new RocketMQMessageListenerAdapter(bean, method));
        pushConsumerBuilder.addSubscription(new Subscription(listenerConfig.getTopic(),
            new FilterExpression(listenerConfig.getFilterExpression(), listenerConfig.getFilterExpressionType())));
        pushConsumerBuilder.setMaxCacheMessageCount(listenerConfig.getMaxCacheMessageCount());
        pushConsumerBuilder.setMaxCacheMessageSizeInBytes(listenerConfig.getMaxCacheMessageSizeInBytes());
        pushConsumerBuilder.setConsumptionThreadCount(listenerConfig.getConsumptionThreadCount());
        return pushConsumerBuilder;
    }

    private RocketMQBatchMessageListenerContainer buildBatchMessageListenerContainer(Object bean, Method method, RocketMQBatchMessageListenerConfig listenerConfig) {
        if (rocketMQConsumerManagerProcessor != null) {
            rocketMQConsumerManagerProcessor.initialize(listenerConfig);
        }
        BatchMessageListenerAdapter batchListenerAdapter = new BatchMessageListenerAdapter(bean, method);
        return new RocketMQBatchMessageListenerContainer(
            batchListenerAdapter, listenerConfig);
    }

    private RocketMQMessageListenerConfig copy(RocketMQMessageListenerConfig src) {
        RocketMQMessageListenerConfig target = new RocketMQMessageListenerConfig();
        target.setConsumerGroup(src.getConsumerGroup());
        target.setTopic(src.getTopic());
        target.setFilterExpressionType(src.getFilterExpressionType());
        target.setFilterExpression(src.getFilterExpression());
        target.setMaxCacheMessageCount(src.getMaxCacheMessageCount());
        target.setMaxCacheMessageSizeInBytes(src.getMaxCacheMessageSizeInBytes());
        target.setConsumptionThreadCount(src.getConsumptionThreadCount());
        return target;
    }

    private RocketMQBatchMessageListenerConfig copy(RocketMQBatchMessageListenerConfig src) {
        RocketMQBatchMessageListenerConfig target = new RocketMQBatchMessageListenerConfig();
        target.setConsumerGroup(src.getConsumerGroup());
        target.setTopic(src.getTopic());
        target.setFilterExpressionType(src.getFilterExpressionType());
        target.setFilterExpression(src.getFilterExpression());
        target.setMaxMessageNum(src.getMaxMessageNum());
        target.setInvisibleDurationMs(src.getInvisibleDurationMs());
        target.setConsumptionThreadCount(src.getConsumptionThreadCount());
        return target;
    }

    @Data
    static class ConsumerGroupTopic {
        private final String consumerGroup;
        private final String topic;
    }

    @AllArgsConstructor
    @Data
    static class PushConsumerDefinition {
        private final Object bean;
        private final Method method;
        private final RocketMQMessageListenerConfig listenerConfig;
        private PushConsumerBuilder pushConsumerBuilder;
    }

    @AllArgsConstructor
    @Data
    static class BatchConsumerDefinition {
        private final Object bean;
        private final Method method;
        private final RocketMQBatchMessageListenerConfig listenerConfig;
        private RocketMQBatchMessageListenerContainer batchListenerContainer;
    }

}
