package fun.fengwk.convention4j.springboot.starter.rocketmq;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.rocketmq.MessageBuilder;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.SpanContextBean;
import fun.fengwk.convention4j.tracer.propagation.inject.Inject;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
@AutoService(Inject.class)
public class MessageBuilderInject implements Inject<MessageBuilder> {

    static final Format<MessageBuilder> MESSAGE_BUILDER_INJECT = new Format<>() {};

    @Override
    public Format<MessageBuilder> format() {
        return MESSAGE_BUILDER_INJECT;
    }

    @Override
    public void inject(SpanContext spanContext, MessageBuilder messageBuilder) {
        if (spanContext == null || messageBuilder == null) {
            return;
        }

        SpanContextBean spanContextBean = SpanContextBean.createFromSpanContext(spanContext);
        if (spanContextBean == null) {
            return;
        }

        messageBuilder.addProperty(PropagationConstants.MESSAGE_SPAN_CONTEXT_KEY, spanContextBean.serialize());
    }

}
