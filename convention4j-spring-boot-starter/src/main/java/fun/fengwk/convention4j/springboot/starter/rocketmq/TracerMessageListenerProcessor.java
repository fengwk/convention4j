package fun.fengwk.convention4j.springboot.starter.rocketmq;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.rocketmq.MessageListenerProcessor;
import fun.fengwk.convention4j.common.rocketmq.MessageListenerProcessorContext;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.Data;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author fengwk
 */
@AutoService(MessageListenerProcessor.class)
public class TracerMessageListenerProcessor implements MessageListenerProcessor {

    private static final String TRACER_CONTEXT = "TRACER_CONTEXT";

    @Override
    public void preProcess(MessageView messageView, MessageListenerProcessorContext context) {
        Tracer tracer = GlobalTracer.get();
        SpanContext spanContext = tracer.extract(MessageBuilderExtract.MESSAGE_VIEW_EXTRACT, messageView);
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("rocketmq_consume_" + messageView.getTopic())
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
            .withTag("rocketmq.topic", messageView.getTopic())
            .withTag("rocketmq.message_id", messageView.getMessageId().toString());
        if (spanContext != null) {
            spanBuilder.asChildOf(spanContext);
        }
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        context.addProperty(TRACER_CONTEXT, new Context(span, scope));
    }

    @Override
    public void postProcess(MessageView messageView, MessageListenerProcessorContext context) {
        Context tracerContext = (Context) context.getProperty(TRACER_CONTEXT);
        if (tracerContext != null) {
            Scope scope = tracerContext.getScope();
            scope.close();
            Span span = tracerContext.getSpan();
            span.finish();
        }
    }

    @Data
    static class Context {

        private final Span span;
        private final Scope scope;

    }

}
