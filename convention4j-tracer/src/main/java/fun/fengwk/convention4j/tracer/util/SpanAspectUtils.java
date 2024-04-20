package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.function.Func0T1;
import io.opentracing.Scope;
import io.opentracing.Span;
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
        Func0T1<R, T> executor, SpanInfo spanInfo) throws T {
        Span span = TracerUtils.startSpan(spanInfo);
        if (span == null) {
            return executor.apply();
        }
        try (Scope ignored = GlobalTracer.get().activateSpan(span)) {
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
