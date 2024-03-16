package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.clock.SystemClock;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import fun.fengwk.convention4j.common.util.ListUtils;
import fun.fengwk.convention4j.common.util.OrderedObject;
import fun.fengwk.convention4j.tracer.Reference;
import fun.fengwk.convention4j.tracer.TracerImpl;
import fun.fengwk.convention4j.tracer.finisher.SpanFinisher;
import fun.fengwk.convention4j.tracer.propagation.TracerTransformer;
import fun.fengwk.convention4j.tracer.scope.ConventionScopeManager;
import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class TracerUtils {

    private static final List<SpanInitializer> SPAN_INITIALIZERS
        = LazyServiceLoader.loadServiceIgnoreLoadFailed(SpanInitializer.class);

    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String TAG_SPAN_ALIAS = "span.alias";

    private TracerUtils() {}

    public static void initializeGlobalTracer(SpanFinisher finisher) {
        TracerTransformer tracerTransformer = new TracerTransformer();
        List<ConventionScopeManager> scopeManagers = LazyServiceLoader
            .loadServiceIgnoreLoadFailed(ConventionScopeManager.class);
        OrderedObject.sort(scopeManagers);
        ConventionScopeManager priorityScopeManager = ListUtils.getFirst(scopeManagers);
        TracerImpl tracer = new TracerImpl(new SystemClock(), priorityScopeManager, tracerTransformer, finisher);
        GlobalTracer.registerIfAbsent(tracer);
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
        return generateUuid();
    }

    public static String generateSpanId() {
        return generateUuid();
    }

    private static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Map<String, String> setMDC(SpanContext spanContext) {
        Map<String, String> storeMdc = new HashMap<>();
        storeMdc.put(TRACE_ID, MDC.get(TRACE_ID));
        storeMdc.put(SPAN_ID, MDC.get(SPAN_ID));
        if (spanContext != null) {
            MDC.put(TRACE_ID, spanContext.toTraceId());
            MDC.put(SPAN_ID, spanContext.toSpanId());
        }
        return storeMdc;
    }

    public static void clearMDC(Map<String, String> storeMdc) {
        String storeTraceId = storeMdc.get(TRACE_ID);
        String storeSpanId = storeMdc.get(SPAN_ID);
        if (StringUtils.isBlank(storeTraceId)) {
            MDC.remove(TRACE_ID);
        } else {
            MDC.put(TRACE_ID, storeTraceId);
        }
        if (StringUtils.isBlank(storeSpanId)) {
            MDC.remove(SPAN_ID);
        } else {
            MDC.put(SPAN_ID, storeSpanId);
        }
    }

}
