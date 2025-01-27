package fun.fengwk.convention4j.springboot.starter.rocketmq;

import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * @author fengwk
 */
public class RocketMQPropertiesChangedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 4811301580149207096L;

    public RocketMQPropertiesChangedEvent(RocketMQProperties properties) {
        super(properties);
    }

    public RocketMQProperties getRocketMQProperties() {
        return (RocketMQProperties) getSource();
    }

}
