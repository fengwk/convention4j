package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.SpanContextImpl;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.propagation.extract.Extract;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author fengwk
 */
@AutoService(Extract.class)
public class ServerWebExchangeExtract implements Extract<ServerWebExchange> {

    public static final Format<ServerWebExchange> FORMAT = new Format<>() {};

    @Override
    public Format<ServerWebExchange> format() {
        return FORMAT;
    }

    @Override
    public SpanContext extract(ServerWebExchange exchange) {
        if (exchange == null) {
            return null;
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String traceId = headers.getFirst(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME);
        String spanId = headers.getFirst(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME);
        String baggage = headers.getFirst(PropagationConstants.BAGGAGE_HEADER_NAME);
        if (StringUtils.isNotBlank(traceId)) {
            return new SpanContextImpl(traceId, spanId, TracerUtils.deserializeHttpPropagationBaggage(baggage));
        }
        return null;
    }

}
