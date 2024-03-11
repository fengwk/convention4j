package fun.fengwk.convention4j.tracer.util;

import com.google.auto.service.AutoService;
import io.opentracing.Span;
import org.slf4j.MDC;

/**
 * @author fengwk
 */
@AutoService(SpanInitializer.class)
public class MDCSpanInitializer implements SpanInitializer {

    private static final String TRACE = "trace";

    @Override
    public void initializeSpan(Span span) {
        MDC.put(TRACE, " - " + span.context().toTraceId() + ":" + span.context().toSpanId());
    }

}
