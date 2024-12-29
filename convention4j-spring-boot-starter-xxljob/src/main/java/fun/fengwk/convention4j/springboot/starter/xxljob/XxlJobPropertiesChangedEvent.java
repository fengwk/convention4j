package fun.fengwk.convention4j.springboot.starter.xxljob;

import org.springframework.context.ApplicationEvent;

/**
 * @author fengwk
 */
public class XxlJobPropertiesChangedEvent extends ApplicationEvent {

    public XxlJobPropertiesChangedEvent(XxlJobProperties source) {
        super(source);
    }

    public XxlJobProperties getXxlJobProperties() {
        return (XxlJobProperties) source;
    }

}
