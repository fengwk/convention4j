package fun.fengwk.convention4j.common.rocketmq;

/**
 * @author fengwk
 */
public interface RocketMQConsumerManagerProcessor {

    /**
     * 后处理RocketMQMessageListenerConfig
     * 
     * @param listenerConfig RocketMQMessageListenerConfig
     */
    default void initialize(RocketMQMessageListenerConfig listenerConfig) {}

    /**
     * 后处理RocketMQBatchMessageListenerConfig
     *
     * @param listenerConfig RocketMQBatchMessageListenerConfig
     */
    default void initialize(RocketMQBatchMessageListenerConfig listenerConfig) {}
    
}
