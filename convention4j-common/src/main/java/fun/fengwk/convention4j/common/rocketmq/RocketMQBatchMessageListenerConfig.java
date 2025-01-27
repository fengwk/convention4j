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

}
