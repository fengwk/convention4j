package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class RocketMQMessageListenerConfig extends AbstractRocketMQMessageListenerConfig {

    /**
     * 最大缓存消息数
     */
    private Integer maxCacheMessageCount;

    /**
     * 最大缓存消息字节数
     */
    private Integer maxCacheMessageSizeInBytes;

    /**
     * 并行消费线程数
     */
    private Integer consumptionThreadCount;

    public static RocketMQMessageListenerConfig copy(RocketMQMessageListenerConfig src) {
        if (src == null) {
            return null;
        }
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

}
