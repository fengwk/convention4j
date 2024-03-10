package fun.fengwk.convention4j.tracer.propagation.inject;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.tracer.propagation.Formats;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.SpanContextBean;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.apache.rocketmq.common.message.Message;

/**
 * @author fengwk
 */
@AutoService(Inject.class)
public class MessageInject implements Inject<Message> {

    @Override
    public Format<Message> format() {
        return Formats.MESSAGE_INJECT;
    }

    @Override
    public void inject(SpanContext spanContext, Message message) {
        if (spanContext == null || message == null) {
            return;
        }

        SpanContextBean spanContextBean = SpanContextBean.createFromSpanContext(spanContext);
        if (spanContextBean == null) {
            return;
        }

        message.putUserProperty(PropagationConstants.MESSAGE_SPAN_CONTEXT_KEY, spanContextBean.serialize());
    }

}
