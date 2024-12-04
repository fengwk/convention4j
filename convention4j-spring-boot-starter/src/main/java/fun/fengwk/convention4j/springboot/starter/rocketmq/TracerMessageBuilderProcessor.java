//package fun.fengwk.convention4j.springboot.starter.rocketmq;
//
//import fun.fengwk.convention4j.common.rocketmq.MessageBuilderProcessor;
//import io.opentracing.Span;
//import io.opentracing.Tracer;
//import io.opentracing.util.GlobalTracer;
//import org.apache.rocketmq.client.apis.message.MessageBuilder;
//
///**
// * @author fengwk
// */
//// 直接从TracerProducer注入而不使用MessageBuilder的SPI，这样可以确保使用Producer时能够正确携带参数
////@AutoService(MessageBuilderProcessor.class)
//public class TracerMessageBuilderProcessor implements MessageBuilderProcessor {
//
//    @Override
//    public void postProcess(MessageBuilder messageBuilder) {
//        Tracer tracer = GlobalTracer.get();
//        Span activeSpan = tracer.activeSpan();
//        if (activeSpan != null) {
//            tracer.inject(activeSpan.context(), MessageBuilderInject.MESSAGE_BUILDER_INJECT, messageBuilder);
//        }
//    }
//
//}
