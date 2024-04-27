package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.util.GlobalTracer;
import lombok.AllArgsConstructor;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class TracerFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Span activeSpan = GlobalTracer.get().activeSpan();
        if (activeSpan != null) {
            SpanContext context = activeSpan.context();
            template.header(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME, context.toTraceId());
            template.header(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME, context.toSpanId());
            template.header(PropagationConstants.BAGGAGE_HEADER_NAME,
                TracerUtils.serializeHttpPropagationBaggage(context.baggageItems()));
        }
    }

}
