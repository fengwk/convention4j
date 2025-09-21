package fun.fengwk.convention4j.tracer.scope.aspect;

import fun.fengwk.convention4j.common.lang.StringUtils;
import io.opentracing.SpanContext;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

import static fun.fengwk.convention4j.tracer.util.TracerUtils.SPAN_ID;
import static fun.fengwk.convention4j.tracer.util.TracerUtils.TRACE_ID;

/**
 * @author fengwk
 */
public class MDCThreadScopeAspect implements ThreadScopeAspect {

    @Override
    public Map<String, String> onEnter(SpanContext spanContext) {
        Map<String, String> storeMdc = new HashMap<>();
        storeMdc.put(TRACE_ID, MDC.get(TRACE_ID));
        storeMdc.put(SPAN_ID, MDC.get(SPAN_ID));
        if (spanContext != null) {
            MDC.put(TRACE_ID, spanContext.toTraceId());
            MDC.put(SPAN_ID, spanContext.toSpanId());
        }
        return storeMdc;
    }

    @Override
    public void onExit(Map<String, String> store) {
        String storeTraceId = store == null ? null : store.get(TRACE_ID);
        String storeSpanId = store == null ? null : store.get(SPAN_ID);
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
