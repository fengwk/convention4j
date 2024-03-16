package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.function.Func0T1;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public class SpanAspectUtils {

    private SpanAspectUtils() {}

    public static <R, T extends Throwable> R execute(
        Func0T1<R, T> executor, SpanAspectInfo spanAspectInfo) throws T {
        Tracer tracer = GlobalTracer.get();
        Span activeSpan = tracer.activeSpan();
        SpanContext parentSpanContext = NullSafe.map(activeSpan, Span::context);

        if (StringUtils.isBlank(spanAspectInfo.getOperationName())) {
            log.warn("Span aspect info operation name is blank, skip span aspect, spanAspectInfo: {}", spanAspectInfo);
            return executor.apply();
        }

        if (parentSpanContext == null) {
            switch (spanAspectInfo.getPropagation()) {
                case REQUIRED:
                    break;
                case SUPPORTS:
                    return executor.apply();
                default:
                    throw new IllegalStateException("Unsupported span propagation: " + spanAspectInfo.getPropagation());
            }
        }

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanAspectInfo.getOperationName());
        if (StringUtils.isNotBlank(spanAspectInfo.getKind())) {
            spanBuilder.withTag(Tags.SPAN_KIND, spanAspectInfo.getKind());
        }
        if (StringUtils.isNotBlank(spanAspectInfo.getAlias())) {
            spanBuilder.withTag(TracerUtils.TAG_SPAN_ALIAS, spanAspectInfo.getAlias());
        }
        if (parentSpanContext != null) {
            spanBuilder.asChildOf(parentSpanContext);
        }
        Span span = spanBuilder.start();
        try (Scope ignored = tracer.activateSpan(span)) {
            R result = executor.apply();
            span.setTag(Tags.ERROR, false);
            return result;
        } catch (Throwable err) {
            span.setTag(Tags.ERROR, true);
            span.log(err.getMessage());
            throw err;
        } finally {
            span.finish();
        }
    }

}
