package fun.fengwk.convention4j.springboot.starter.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.util.TracerUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class TraceClassicConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> trace = new HashMap<>();
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        String traceId = mdcPropertyMap.get(TracerUtils.TRACE_ID);
        String spanId = mdcPropertyMap.get(TracerUtils.SPAN_ID);
        if (StringUtils.isNotBlank(traceId)) {
            trace.put(TracerUtils.TRACE_ID, traceId);
        }
        if (StringUtils.isNotBlank(spanId)) {
            trace.put(TracerUtils.SPAN_ID, spanId);
        }
        return trace.isEmpty() ? "" : JsonUtils.toJson(trace) + "\n";
    }

}
