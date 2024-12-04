package fun.fengwk.convention4j.springboot.starter.rocketmq;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.SpanContextBean;
import fun.fengwk.convention4j.tracer.propagation.extract.Extract;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author fengwk
 */
@AutoService(Extract.class)
public class MessageBuilderExtract implements Extract<MessageView> {

    static final Format<MessageView> MESSAGE_VIEW_EXTRACT = new Format<>() {};

    @Override
    public Format<MessageView> format() {
        return MESSAGE_VIEW_EXTRACT;
    }

    @Override
    public SpanContext extract(MessageView messageView) {
        if (messageView == null) {
            return null;
        }

        String spanContextValue = messageView.getProperties().get(PropagationConstants.MESSAGE_SPAN_CONTEXT_KEY);
        if (StringUtils.isBlank(spanContextValue)) {
            return null;
        }

        SpanContextBean spanContextBean = SpanContextBean.deserialize(spanContextValue);
        return NullSafe.map(spanContextBean, SpanContextBean::toSpanContext);
    }

}
