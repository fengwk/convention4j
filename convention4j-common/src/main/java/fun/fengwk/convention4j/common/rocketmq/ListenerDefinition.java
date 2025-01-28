package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Data
public class ListenerDefinition {
    private final Object bean;
    private final Method method;
    private final RocketMQMessageListenerConfig listenerConfig;
}
