package fun.fengwk.convention4j.springboot.starter.rocketmq;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.rocketmq.BatchMessageListenerProcessor;
import fun.fengwk.convention4j.common.rocketmq.ProcessorContext;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.tag.ObjectTag;
import io.opentracing.*;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoService(BatchMessageListenerProcessor.class)
public class TracerBatchMessageListenerProcessor implements BatchMessageListenerProcessor {

    @Override
    public void preProcess(List<MessageView> messageViewList, ProcessorContext context) {
        Tracer tracer = GlobalTracer.get();

        MessageView firstMessageView = messageViewList.get(0);

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("rocketmq_batch_consumer_" + firstMessageView.getTopic())
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
            .withTag(RocketMQTracerTags.TOPIC, firstMessageView.getTopic())
            .withTag(new ObjectTag<>(RocketMQTracerTags.MESSAGE_IDS),
                NullSafe.map2List(messageViewList, view -> view.getMessageId().toString()));

        for (MessageView messageView : messageViewList) {
            SpanContext spanContext = tracer.extract(MessageBuilderExtract.MESSAGE_VIEW_EXTRACT, messageView);
            if (spanContext != null) {
                spanBuilder.addReference(References.FOLLOWS_FROM, spanContext);
            }
        }

        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        context.addProperty(RocketMQTracerContext.TRACER_CONTEXT, new RocketMQTracerContext(span, scope));
    }

    @Override
    public void postProcess(List<MessageView> messageViewList, ProcessorContext context,
                            Collection<MessageView> acks) {
        RocketMQTracerContext tracerContext = (RocketMQTracerContext) context.getProperty(RocketMQTracerContext.TRACER_CONTEXT);
        if (tracerContext != null) {
            Scope scope = tracerContext.getScope();
            Span span = tracerContext.getSpan();
            span.setTag(Tags.ERROR, acks.size() < messageViewList.size());
            scope.close();
            span.finish();
        }
    }

}
