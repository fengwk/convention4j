package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.io.IOException;
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

    private final ConcurrentMap<ConsumerGroupTopic, PushConsumer> consumerRegistry = new ConcurrentHashMap<>();
    private final ConcurrentMap<ConsumerGroupTopic, RocketMQBatchMessageListenerContainer> batchConsumerRegistry = new ConcurrentHashMap<>();

    public DefaultRocketMQConsumerManager(RocketMQConsumerRegistry registry,
                                          ClientConfiguration clientConfiguration,
                                          RocketMQConsumerManagerProcessor rocketMQConsumerManagerProcessor) {
        super(registry);
        this.clientConfiguration = Objects.requireNonNull(clientConfiguration);
        this.rocketMQConsumerManagerProcessor = rocketMQConsumerManagerProcessor;
    }

    @Override
    public synchronized void refreshConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException {
        ListenerDefinition listenerDefinition = registry.getListenerDefinition(consumerGroupTopic);
        if (listenerDefinition == null) {
            throw new IllegalArgumentException(String.format("listener definition [%s | %s] not registered",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }
        closeConsumer(consumerGroupTopic);
        startConsumer(listenerDefinition);
    }

    @Override
    public synchronized void refreshBatchConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException {
        BatchListenerDefinition batchListenerDefinition = registry.getBatchListenerDefinition(consumerGroupTopic);
        if (batchListenerDefinition == null) {
            throw new IllegalArgumentException(String.format("batch listener definition [%s | %s] not registered",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }
        closeBatchConsumer(consumerGroupTopic);
        startBatchConsumer(batchListenerDefinition);
    }

    @Override
    public synchronized void close() {
        for (ConsumerGroupTopic consumerGroupTopic : registry.listenerConsumerGroupTopics()) {
            closeConsumer(consumerGroupTopic);
        }
        for (ConsumerGroupTopic consumerGroupTopic : registry.batchListenerConsumerGroupTopics()) {
            closeConsumer(consumerGroupTopic);
        }
    }

    private void startConsumer(ListenerDefinition listenerDefinition) throws ClientException {
        RocketMQMessageListenerConfig listenerConfig = RocketMQMessageListenerConfig.copy(
            listenerDefinition.getListenerConfig());
        if (rocketMQConsumerManagerProcessor != null) {
            rocketMQConsumerManagerProcessor.initialize(listenerConfig);
        }
        PushConsumerBuilder pushConsumerBuilder = new PushConsumerBuilder();
        pushConsumerBuilder.setConsumerGroup(listenerConfig.getConsumerGroup());
        pushConsumerBuilder.setListener(new RocketMQMessageListenerAdapter(
            listenerDefinition.getBean(), listenerDefinition.getMethod()));
        pushConsumerBuilder.addSubscription(new Subscription(listenerConfig.getTopic(),
            new FilterExpression(listenerConfig.getFilterExpression(), listenerConfig.getFilterExpressionType())));
        pushConsumerBuilder.setMaxCacheMessageCount(listenerConfig.getMaxCacheMessageCount());
        pushConsumerBuilder.setMaxCacheMessageSizeInBytes(listenerConfig.getMaxCacheMessageSizeInBytes());
        pushConsumerBuilder.setConsumptionThreadCount(listenerConfig.getConsumptionThreadCount());
        PushConsumer pushConsumer = pushConsumerBuilder.build(clientConfiguration);
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(
            listenerConfig.getConsumerGroup(), listenerConfig.getTopic());
        consumerRegistry.put(consumerGroupTopic, pushConsumer);
    }

    private void startBatchConsumer(BatchListenerDefinition batchListenerDefinition) throws ClientException {
        RocketMQBatchMessageListenerConfig batchListenerConfig = RocketMQBatchMessageListenerConfig.copy(
            batchListenerDefinition.getListenerConfig());
        if (rocketMQConsumerManagerProcessor != null) {
            rocketMQConsumerManagerProcessor.initialize(batchListenerConfig);
        }
        BatchMessageListenerAdapter batchListenerAdapter = new BatchMessageListenerAdapter(
            batchListenerDefinition.getBean(), batchListenerDefinition.getMethod());
        RocketMQBatchMessageListenerContainer container = new RocketMQBatchMessageListenerContainer(
            batchListenerAdapter, batchListenerConfig);
        container.start(clientConfiguration);
        ConsumerGroupTopic consumerGroupTopic = new ConsumerGroupTopic(
            batchListenerConfig.getConsumerGroup(), batchListenerConfig.getTopic());
        batchConsumerRegistry.put(consumerGroupTopic, container);
    }

    private void closeConsumer(ConsumerGroupTopic consumerGroupTopic) {
        PushConsumer pushConsumer = consumerRegistry.remove(consumerGroupTopic);
        if (pushConsumer != null) {
            try {
                pushConsumer.close();
            } catch (IOException ex) {
                log.error("close push consumer error", ex);
            }
        }
    }

    private void closeBatchConsumer(ConsumerGroupTopic consumerGroupTopic) {
        RocketMQBatchMessageListenerContainer batchPushConsumer = batchConsumerRegistry.remove(consumerGroupTopic);
        if (batchPushConsumer != null) {
            try {
                batchPushConsumer.close();
            } catch (IOException ex) {
                log.error("close batch message listener container error", ex);
            }
        }
    }

}
