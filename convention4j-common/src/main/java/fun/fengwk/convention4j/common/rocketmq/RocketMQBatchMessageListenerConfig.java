package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class RocketMQBatchMessageListenerConfig extends AbstractRocketMQMessageListenerConfig {

    /**
     * 最多获取的消息数
     */
    private Integer maxMessageNum;

    /**
     * 消息对其它消费者不可见的时长，单位毫秒
     */
    private Long invisibleDurationMs;

    /**
     * 并行消费线程数
     */
    private Integer consumptionThreadCount;

    public static RocketMQBatchMessageListenerConfig copy(RocketMQBatchMessageListenerConfig src) {
        if (src == null) {
            return null;
        }
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

}
