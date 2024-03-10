package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.function.Func0T1;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
public class SpanAspectUtils {

    private SpanAspectUtils() {}

    public static <R, T extends Throwable> R execute(
        Func0T1<R, T> executor, Method method, SpanAspectInfo spanAspectInfo) throws T {
        Tracer tracer = GlobalTracer.get();
        Span activeSpan = tracer.activeSpan();
        SpanContext parentSpanContext = NullSafe.map(activeSpan, Span::context);

        String operationName = buildOperationName(method, spanAspectInfo);
        if (StringUtils.isBlank(operationName)) {
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

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName);
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

    private static String buildOperationName(Method method, SpanAspectInfo spanAspectInfo) {
        if (StringUtils.isNotBlank(spanAspectInfo.getValue())) {
            return spanAspectInfo.getValue();
        }

        if (method != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            StringBuilder sb = new StringBuilder(
                method.getDeclaringClass().getSimpleName());
            sb.append('#').append(method.getName()).append('(');
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(parameterTypes[i].getSimpleName());
            }
            sb.append(')');
            return sb.toString();

        }

        return "";
    }

}
