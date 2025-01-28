package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fengwk
 */
@Slf4j
public class TestRocketMQConsumerManager extends AbstractRocketMQConsumerManager {

    private final TestRocketMQBroker broker;
    private final List<TestRocketMQConsumer> consumers = new CopyOnWriteArrayList<>();

    public TestRocketMQConsumerManager(RocketMQConsumerRegistry registry, TestRocketMQBroker broker) {
        super(registry);
        this.broker = Objects.requireNonNull(broker);
    }

    @Override
    public void refreshConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException {
        ListenerDefinition listenerDefinition = registry.getListenerDefinition(consumerGroupTopic);
        if (listenerDefinition == null) {
            throw new IllegalArgumentException(String.format("listener definition [%s | %s] not registered",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }

        RocketMQMessageListenerConfig listenerConfig = listenerDefinition.getListenerConfig();
        String topic = listenerConfig.getTopic();
        String consumerGroup = listenerConfig.getConsumerGroup();
        FilterExpression filterExpression = new FilterExpression(
            listenerConfig.getFilterExpression(), listenerConfig.getFilterExpressionType());

        if (broker.registerQueueIfNecessary(topic, consumerGroup)) {
            RocketMQMessageListenerAdapter messageListener = new RocketMQMessageListenerAdapter(
                listenerDefinition.getBean(), listenerDefinition.getMethod());
            TestRocketMQConsumer consumer = new TestRocketMQConsumer(broker, topic, consumerGroup, messageListener, filterExpression);
            consumer.start();
            consumers.add(consumer);
        }
    }

    @Override
    public void refreshBatchConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException {
        BatchListenerDefinition batchListenerDefinition = registry.getBatchListenerDefinition(consumerGroupTopic);
        if (batchListenerDefinition == null) {
            throw new IllegalArgumentException(String.format("batch listener definition [%s | %s] not registered",
                consumerGroupTopic.getConsumerGroup(), consumerGroupTopic.getTopic()));
        }

        RocketMQBatchMessageListenerConfig batchListenerConfig = batchListenerDefinition.getListenerConfig();
        String topic = batchListenerConfig.getTopic();
        String consumerGroup = batchListenerConfig.getConsumerGroup();
        FilterExpression filterExpression = new FilterExpression(
            batchListenerConfig.getFilterExpression(), batchListenerConfig.getFilterExpressionType());

        if (broker.registerQueueIfNecessary(topic, consumerGroup)) {
            // 也使用单个消费的方式简单实现
            BatchMessageListenerAdapter batchListenerAdapter = new BatchMessageListenerAdapter(
                batchListenerDefinition.getBean(), batchListenerDefinition.getMethod());
            TestRocketMQConsumer consumer = new TestRocketMQConsumer(broker, topic, consumerGroup,
                new BatchMessageListenerAdapterBridge(batchListenerAdapter), filterExpression);
            consumer.start();
            consumers.add(consumer);
        }
    }

    @Override
    public void close() {
        for (TestRocketMQConsumer consumer : consumers) {
            consumer.close();
        }
    }

}
