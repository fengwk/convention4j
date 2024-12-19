package fun.fengwk.convention4j.springboot.starter.rocketmq;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.rocketmq.MessageListenerProcessor;
import fun.fengwk.convention4j.common.rocketmq.ProcessorContext;
import fun.fengwk.convention4j.tracer.tag.ObjectTag;
import io.opentracing.*;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collections;

/**
 * @author fengwk
 */
@AutoService(MessageListenerProcessor.class)
public class TracerMessageListenerProcessor implements MessageListenerProcessor {

    @Override
    public void preProcess(MessageView messageView, ProcessorContext context) {
        Tracer tracer = GlobalTracer.get();
        SpanContext spanContext = tracer.extract(MessageBuilderExtract.MESSAGE_VIEW_EXTRACT, messageView);
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("rocketmq_consumer_" + messageView.getTopic())
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
            .withTag(RocketMQTracerTags.TOPIC, messageView.getTopic())
            .withTag(RocketMQTracerTags.MESSAGE_ID, messageView.getMessageId().toString())
            .withTag(new ObjectTag<>(RocketMQTracerTags.MESSAGE_IDS), Collections.singletonList(messageView.getMessageId().toString()));
        if (spanContext != null) {
            spanBuilder.addReference(References.FOLLOWS_FROM, spanContext);
        }
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        context.addProperty(RocketMQTracerContext.TRACER_CONTEXT, new RocketMQTracerContext(span, scope));
    }

    @Override
    public void postProcess(MessageView messageView, ProcessorContext context, ConsumeResult consumeResult) {
        RocketMQTracerContext tracerContext = (RocketMQTracerContext) context.getProperty(RocketMQTracerContext.TRACER_CONTEXT);
        if (tracerContext != null) {
            Scope scope = tracerContext.getScope();
            Span span = tracerContext.getSpan();
            span.setTag(Tags.ERROR, consumeResult != ConsumeResult.SUCCESS);
            scope.close();
            span.finish();
        }
    }

}
