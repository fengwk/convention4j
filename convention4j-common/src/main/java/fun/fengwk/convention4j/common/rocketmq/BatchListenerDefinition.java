package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Data
public class BatchListenerDefinition {
    private final Object bean;
    private final Method method;
    private final RocketMQBatchMessageListenerConfig listenerConfig;
}
