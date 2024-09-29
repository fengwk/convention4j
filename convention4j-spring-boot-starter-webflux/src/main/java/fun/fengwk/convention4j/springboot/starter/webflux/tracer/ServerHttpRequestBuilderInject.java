package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.inject.Inject;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author fengwk
 */
@AutoService(Inject.class)
public class ServerHttpRequestBuilderInject implements Inject<ServerHttpRequest.Builder> {

    public static final Format<ServerHttpRequest.Builder> FORMAT = new Format<>() {};

    @Override
    public Format<ServerHttpRequest.Builder> format() {
        return FORMAT;
    }

    @Override
    public void inject(SpanContext spanContext, ServerHttpRequest.Builder builder) {
        if (spanContext == null || builder == null) {
            return;
        }
        builder.headers(httpHeaders -> {
            httpHeaders.set(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME, spanContext.toTraceId());
            httpHeaders.set(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME, spanContext.toSpanId());
            httpHeaders.set(PropagationConstants.BAGGAGE_HEADER_NAME,
                TracerUtils.serializeHttpPropagationBaggage(spanContext.baggageItems()));
        });
    }

}
