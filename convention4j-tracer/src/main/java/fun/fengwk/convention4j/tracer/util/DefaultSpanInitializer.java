package fun.fengwk.convention4j.tracer.util;

import com.google.auto.service.AutoService;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

/**
 * @author fengwk
 */
@AutoService(SpanInitializer.class)
public class DefaultSpanInitializer implements SpanInitializer {

    @Override
    public void initializeSpan(Span span) {
        SpanInitializer.super.initializeSpan(span);
        span.setTag(Tags.ERROR, false);
    }

}
