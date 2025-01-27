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

}
