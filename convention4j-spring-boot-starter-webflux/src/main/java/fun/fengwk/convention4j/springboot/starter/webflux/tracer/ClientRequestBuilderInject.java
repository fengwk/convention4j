package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.inject.Inject;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * @author fengwk
 */
@AutoService(Inject.class)
public class ClientRequestBuilderInject implements Inject<ClientRequest.Builder> {

    public static final Format<ClientRequest.Builder> FORMAT = new Format<>() {};

    @Override
    public Format<ClientRequest.Builder> format() {
        return FORMAT;
    }

    @Override
    public void inject(SpanContext spanContext, ClientRequest.Builder builder) {
        if (spanContext == null || builder == null) {
            return;
        }
        builder.headers(headers -> {
            headers.set(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME, spanContext.toTraceId());
            headers.set(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME, spanContext.toSpanId());
            headers.set(PropagationConstants.BAGGAGE_HEADER_NAME,
                TracerUtils.serializeHttpPropagationBaggage(spanContext.baggageItems()));
        });
    }

}
