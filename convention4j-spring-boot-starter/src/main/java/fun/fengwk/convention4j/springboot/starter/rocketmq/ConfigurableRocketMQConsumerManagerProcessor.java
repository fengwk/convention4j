package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.RocketMQBatchMessageListenerConfig;
import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerManagerProcessor;
import fun.fengwk.convention4j.common.rocketmq.RocketMQMessageListenerConfig;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class ConfigurableRocketMQConsumerManagerProcessor implements RocketMQConsumerManagerProcessor {

    private final RocketMQProperties rocketMQProperties;

    public ConfigurableRocketMQConsumerManagerProcessor(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void initialize(RocketMQMessageListenerConfig listenerConfig) {
        String consumerGroup = listenerConfig.getConsumerGroup();
        String topic = listenerConfig.getTopic();

        List<RocketMQMessageListenerConfig> consumerConfigs = rocketMQProperties.getConsumers();
        for (RocketMQMessageListenerConfig consumerConfig : NullSafe.of(consumerConfigs)) {
            if (Objects.equals(consumerConfig.getConsumerGroup(), consumerGroup)
                && Objects.equals(consumerConfig.getTopic(), topic)) {
                if (consumerConfig.getFilterExpressionType() != null) {
                    listenerConfig.setFilterExpressionType(consumerConfig.getFilterExpressionType());
                }
                if (consumerConfig.getFilterExpression() != null) {
                    listenerConfig.setFilterExpression(consumerConfig.getFilterExpression());
                }
                if (consumerConfig.getMaxCacheMessageCount() != null) {
                    listenerConfig.setMaxCacheMessageCount(consumerConfig.getMaxCacheMessageCount());
                }
                if (consumerConfig.getMaxCacheMessageSizeInBytes() != null) {
                    listenerConfig.setMaxCacheMessageSizeInBytes(consumerConfig.getMaxCacheMessageSizeInBytes());
                }
                if (consumerConfig.getConsumptionThreadCount() != null) {
                    listenerConfig.setConsumptionThreadCount(consumerConfig.getConsumptionThreadCount());
                }
                break;
            }
        }
    }

    @Override
    public void initialize(RocketMQBatchMessageListenerConfig listenerConfig) {
        String consumerGroup = listenerConfig.getConsumerGroup();
        String topic = listenerConfig.getTopic();

        List<RocketMQBatchMessageListenerConfig> consumerConfigs = rocketMQProperties.getBatchConsumers();
        for (RocketMQBatchMessageListenerConfig consumerConfig : NullSafe.of(consumerConfigs)) {
            if (Objects.equals(consumerConfig.getConsumerGroup(), consumerGroup)
                && Objects.equals(consumerConfig.getTopic(), topic)) {
                if (consumerConfig.getFilterExpressionType() != null) {
                    listenerConfig.setFilterExpressionType(consumerConfig.getFilterExpressionType());
                }
                if (consumerConfig.getFilterExpression() != null) {
                    listenerConfig.setFilterExpression(consumerConfig.getFilterExpression());
                }
                if (consumerConfig.getMaxMessageNum() != null) {
                    listenerConfig.setMaxMessageNum(consumerConfig.getMaxMessageNum());
                }
                if (consumerConfig.getInvisibleDurationMs() != null) {
                    listenerConfig.setInvisibleDurationMs(consumerConfig.getInvisibleDurationMs());
                }
                if (consumerConfig.getConsumptionThreadCount() != null) {
                    listenerConfig.setConsumptionThreadCount(consumerConfig.getConsumptionThreadCount());
                }
                break;
            }
        }
    }

}
