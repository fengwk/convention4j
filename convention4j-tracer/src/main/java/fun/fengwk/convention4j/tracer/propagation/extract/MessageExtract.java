package fun.fengwk.convention4j.tracer.propagation.extract;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.propagation.Formats;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.SpanContextBean;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.apache.rocketmq.common.message.Message;

/**
 * @author fengwk
 */
@AutoService(Extract.class)
public class MessageExtract implements Extract<Message> {

    @Override
    public Format<Message> format() {
        return Formats.MESSAGE_EXTRACT;
    }

    @Override
    public SpanContext extract(Message message) {
        if (message == null) {
            return null;
        }

        String spanContextValue = message.getUserProperty(PropagationConstants.MESSAGE_SPAN_CONTEXT_KEY);
        if (StringUtils.isBlank(spanContextValue)) {
            return null;
        }

        SpanContextBean spanContextBean = SpanContextBean.deserialize(spanContextValue);
        return NullSafe.map(spanContextBean, SpanContextBean::toSpanContext);
    }

}
