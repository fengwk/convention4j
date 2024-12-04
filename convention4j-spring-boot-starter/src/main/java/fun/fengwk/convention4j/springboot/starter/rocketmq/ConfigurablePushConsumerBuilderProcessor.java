package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.PushConsumerBuilder;
import fun.fengwk.convention4j.common.rocketmq.PushConsumerBuilderProcessor;

/**
 * @author fengwk
 */
public class ConfigurablePushConsumerBuilderProcessor implements PushConsumerBuilderProcessor {

    private final RocketMQProperties rocketMQProperties;

    public ConfigurablePushConsumerBuilderProcessor(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void postProcess(PushConsumerBuilder pushConsumerBuilder) {
        RocketMQProperties.ConsumerConfig consumerConfig = rocketMQProperties.getConsumer();
        if (consumerConfig != null) {
            if (consumerConfig.getMaxCacheMessageCount() != null) {
                pushConsumerBuilder.setMaxCacheMessageCount(consumerConfig.getMaxCacheMessageCount());
            }
            if (consumerConfig.getMaxCacheMessageSizeInBytes() != null) {
                pushConsumerBuilder.setMaxCacheMessageSizeInBytes(consumerConfig.getMaxCacheMessageSizeInBytes());
            }
            if (consumerConfig.getConsumptionThreadCount() != null) {
                pushConsumerBuilder.setConsumptionThreadCount(consumerConfig.getConsumptionThreadCount());
            }
        }
    }

}
