package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.function.Func0T1;
import fun.fengwk.convention4j.common.rocketmq.MessageBuilder;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.SpanPropagation;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
public class TracerProducer implements Producer {

    private final Producer delegate;

    public TracerProducer(Producer delegate) {
        this.delegate = delegate;
    }

    @Override
    public SendReceipt send(Message message) throws ClientException {
        SpanInfo spanInfo = buildSpanInfo(message);
        return executeAndReturn(spanInfo, () -> delegate.send(buildTracerMessage(message)));
    }

    @Override
    public SendReceipt send(Message message, Transaction transaction) throws ClientException {
        SpanInfo spanInfo = buildSpanInfo(message);
        return executeAndReturn(spanInfo, () -> delegate.send(buildTracerMessage(message), transaction));
    }

    @Override
    public CompletableFuture<SendReceipt> sendAsync(Message message) {
        SpanInfo spanInfo = buildSpanInfo(message);
        return executeAndReturn(spanInfo, () -> delegate.sendAsync(buildTracerMessage(message)));
    }

    @Override
    public Transaction beginTransaction() throws ClientException {
        return delegate.beginTransaction();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private static <R, T extends Throwable> R executeAndReturn(SpanInfo spanInfo, Func0T1<R, T> executor) throws T {
        Tracer tracer = GlobalTracer.get();
        return TracerUtils.executeAndReturn(tracer, spanInfo, executor);
    }

    private static SpanInfo buildSpanInfo(Message message) {
        return SpanInfo.builder()
            .operationName("rocketmq_producer_" + message.getTopic())
            .kind(Tags.SPAN_KIND_PRODUCER)
            .propagation(SpanPropagation.REQUIRED)
            .build();
    }

    private Message buildTracerMessage(Message message) {
        MessageBuilder messageBuilder = new MessageBuilder();
        ByteBuffer body = message.getBody();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while (body.hasRemaining()) {
                out.write(body.get());
            }
            messageBuilder.setBody(out.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        messageBuilder.setTopic(message.getTopic());
        messageBuilder.setTag(message.getTag().orElse(null));
        messageBuilder.setMessageGroup(message.getMessageGroup().orElse(null));
        messageBuilder.setDeliveryTimestamp(message.getDeliveryTimestamp().orElse(null));
        for (Map.Entry<String, String> property : message.getProperties().entrySet()) {
            messageBuilder.addProperty(property.getKey(), property.getValue());
        }
        for (String key : message.getKeys()) {
            messageBuilder.addKey(key);
        }

        Tracer tracer = GlobalTracer.get();
        Span activeSpan = tracer.activeSpan();
        if (activeSpan != null) {
            tracer.inject(activeSpan.context(), MessageBuilderInject.MESSAGE_BUILDER_INJECT, messageBuilder);
        }

        return messageBuilder.build();
    }

}
