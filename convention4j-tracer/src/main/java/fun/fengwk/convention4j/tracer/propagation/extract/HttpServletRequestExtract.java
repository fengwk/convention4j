package fun.fengwk.convention4j.tracer.propagation.extract;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.SpanContextImpl;
import fun.fengwk.convention4j.tracer.propagation.Formats;
import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author fengwk
 */
@AutoService(Extract.class)
public class HttpServletRequestExtract implements Extract<HttpServletRequest> {

    @Override
    public Format<HttpServletRequest> format() {
        return Formats.HTTP_SERVLET_REQUEST_EXTRACT;
    }

    @Override
    public SpanContext extract(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String traceId = request.getHeader(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME);
        String spanId = request.getHeader(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME);
        String baggage = request.getHeader(PropagationConstants.BAGGAGE_HEADER_NAME);
        if (StringUtils.isNotBlank(traceId)) {
            return new SpanContextImpl(traceId, spanId, TracerUtils.deserializeHttpPropagationBaggage(baggage));
        }
        return null;
    }

}
