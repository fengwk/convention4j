package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.clock.SystemClock;
import fun.fengwk.convention4j.common.function.Func0T1;
import fun.fengwk.convention4j.common.function.VoidFunc0T1;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.reflect.TypeToken;
import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.common.web.UriUtils;
import fun.fengwk.convention4j.tracer.Reference;
import fun.fengwk.convention4j.tracer.TracerImpl;
import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.finisher.SpanFinisher;
import fun.fengwk.convention4j.tracer.propagation.TracerTransformer;
import fun.fengwk.convention4j.tracer.scope.TtlScopeManager;
import fun.fengwk.convention4j.tracer.scope.aspect.MDCThreadScopeAspect;
import fun.fengwk.convention4j.tracer.scope.aspect.ThreadScopeAspect;
import io.opentracing.*;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class TracerUtils {

    private static final List<SpanInitializer> SPAN_INITIALIZERS = LazyServiceLoader
            .loadServiceIgnoreLoadFailed(SpanInitializer.class);

    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String TAG_SPAN_ALIAS = "span.alias";

    private TracerUtils() {
    }

    public static void initializeGlobalTracer() {
        ThreadScopeAspect threadScopeAspect = new MDCThreadScopeAspect();
        ScopeManager scopeManager = new TtlScopeManager(threadScopeAspect);
        Slf4jSpanFinisher finisher = new Slf4jSpanFinisher();
        Tracer tracer = buildTracer(scopeManager, finisher);
        GlobalTracer.registerIfAbsent(tracer);
    }

    public static Tracer buildTracer(ScopeManager scopeManager, SpanFinisher finisher) {
        TracerTransformer tracerTransformer = new TracerTransformer();
        return new TracerImpl(new SystemClock(), scopeManager, tracerTransformer, finisher);
    }

    public static void initializeRootSpan(Span span) {
        for (SpanInitializer initializer : SPAN_INITIALIZERS) {
            initializer.initializeRootSpan(span);
        }
    }

    public static void initializeSpan(Span span) {
        for (SpanInitializer initializer : SPAN_INITIALIZERS) {
            initializer.initializeSpan(span);
        }
    }

    public static Map<String, String> buildBaggage(Iterable<Map.Entry<String, String>> baggageIt) {
        Map<String, String> baggage = new HashMap<>();
        if (baggageIt != null) {
            for (Map.Entry<String, String> entry : baggageIt) {
                baggage.put(entry.getKey(), entry.getValue());
            }
        }
        return baggage;
    }

    public static String serializeHttpPropagationBaggage(Iterable<Map.Entry<String, String>> baggageIt) {
        Map<String, String> baggage = buildBaggage(baggageIt);
        String baggageJson = JsonUtils.toJson(baggage);
        return UriUtils.encodeUriComponent(baggageJson);
    }

    public static Map<String, String> deserializeHttpPropagationBaggage(String serializedBaggage) {
        String baggageJson = UriUtils.decodeUriComponent(serializedBaggage);
        return JsonUtils.fromJson(baggageJson, new TypeToken<>() {
        });
    }

    public static Reference getChildOfReference(List<Reference> references) {
        if (references == null || references.isEmpty()) {
            return null;
        }
        return references.stream()
                .filter(ref -> Objects.equals(ref.getReferenceType(), References.CHILD_OF))
                .findFirst()
                .orElse(null);
    }

    public static List<Reference> getFollowsFromReferences(List<Reference> references) {
        if (references == null || references.isEmpty()) {
            return Collections.emptyList();
        }
        return references.stream()
                .filter(ref -> Objects.equals(ref.getReferenceType(), References.FOLLOWS_FROM))
                .collect(Collectors.toList());
    }

    public static String generateTraceId() {
        return generateUUID();
    }

    public static String generateSpanId() {
        return generateUUID();
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static SpanContext activeSpanContext(Tracer tracer) {
        Span activeSpan = tracer.activeSpan();
        return NullSafe.map(activeSpan, Span::context);
    }

    public static Span startSpan(Tracer tracer, SpanInfo spanInfo, SpanContext parent) {
        if (spanInfo == null) {
            log.warn("Start span failed, spanInfo is null");
            return null;
        }
        if (StringUtils.isBlank(spanInfo.getOperationName())) {
            log.warn("Start span failed, operation name is blank, spanInfo: {}", spanInfo);
            return null;
        }

        String alias = spanInfo.getOperationName();
        if (StringUtils.isNotBlank(spanInfo.getAlias())) {
            alias = spanInfo.getAlias();
        }

        if (parent == null) {
            switch (spanInfo.getPropagation()) {
                case REQUIRED:
                    break;
                case SUPPORTS:
                    return null;
                default:
                    throw new IllegalStateException("Unsupported span propagation: " + spanInfo.getPropagation());
            }
        }

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanInfo.getOperationName())
                .withTag(TracerUtils.TAG_SPAN_ALIAS, alias);
        if (StringUtils.isNotBlank(spanInfo.getKind())) {
            spanBuilder.withTag(Tags.SPAN_KIND, spanInfo.getKind());
        }
        if (parent != null) {
            spanBuilder.asChildOf(parent);
        }
        return spanBuilder.start();
    }

    public static <R, T extends Throwable> R executeAndReturn(
            Tracer tracer, SpanInfo spanInfo, Func0T1<R, T> executor) throws T {
        Span span = TracerUtils.startSpan(tracer, spanInfo, TracerUtils.activeSpanContext(tracer));
        if (span == null) {
            return executor.apply();
        }
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

    public static <T extends Throwable> void execute(
        Tracer tracer, SpanInfo spanInfo, VoidFunc0T1<T> executor) throws T {
        executeAndReturn(tracer, spanInfo, () -> {
            executor.apply();
            return null;
        });
    }

}
