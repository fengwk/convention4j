package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;

import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractRocketMQConsumerManager implements AutoCloseable {

    protected final RocketMQConsumerRegistry registry;

    protected AbstractRocketMQConsumerManager(RocketMQConsumerRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    /**
     * 启动所有消费者
     */
    public synchronized void start() throws ClientException {
        for (ConsumerGroupTopic consumerGroupTopic : registry.listenerConsumerGroupTopics()) {
            refreshConsumer(consumerGroupTopic);
        }
        for (ConsumerGroupTopic consumerGroupTopic : registry.batchListenerConsumerGroupTopics()) {
            refreshBatchConsumer(consumerGroupTopic);
        }
    }

    /**
     * 刷新消费者，需要start之后才能成功刷新
     * @throws IllegalArgumentException consumerGroupTopic格式错误或无法找到
     */
    public abstract void refreshConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException;

    /**
     * 刷新批量消费者，需要start之后才能成功刷新
     * @throws IllegalArgumentException consumerGroupTopic格式错误或无法找到
     */
    public abstract void refreshBatchConsumer(ConsumerGroupTopic consumerGroupTopic) throws ClientException;

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
