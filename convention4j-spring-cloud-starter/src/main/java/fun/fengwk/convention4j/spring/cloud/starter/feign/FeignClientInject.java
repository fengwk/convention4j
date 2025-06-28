package fun.fengwk.convention4j.spring.cloud.starter.feign;

import com.google.auto.service.AutoService;
import feign.RequestTemplate;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.inject.Inject;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
@AutoService(Inject.class)
public class FeignClientInject implements Inject<RequestTemplate> {

    public static final Format<RequestTemplate> FORMAT = new Format<>() {};

    @Override
    public Format<RequestTemplate> format() {
        return FORMAT;
    }

    @Override
    public void inject(SpanContext spanContext, RequestTemplate template) {
        if (spanContext == null || template == null) {
            return;
        }
        template.header(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME, spanContext.toTraceId());
        template.header(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME, spanContext.toSpanId());
        template.header(PropagationConstants.BAGGAGE_HEADER_NAME,
            TracerUtils.serializeHttpPropagationBaggage(spanContext.baggageItems()));
    }

}
