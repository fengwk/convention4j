package fun.fengwk.convention4j.tracer.finisher;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.Reference;
import fun.fengwk.convention4j.tracer.SpanImpl;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tags;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author fengwk
 */
@Slf4j
public class Slf4jSpanFinisher implements SpanFinisher {

    @Override
    public void finish(SpanImpl span, long finishMicros) {
        try {

            SpanContext context = span.context();

            List<Reference> references = span.getReferences();
            LogBean logBean = new LogBean();
            logBean.setTraceId(context.toTraceId());
            logBean.setSpanId(context.toSpanId());
            logBean.setParentSpanId(
                NullSafe.map2(TracerUtils.getChildOfReference(references),
                    Reference::getSpanContext, SpanContext::toSpanId));
            logBean.setFollowSpanIds(
                NullSafe.map2List(TracerUtils.getFollowsFromReferences(references),
                    r -> r.getSpanContext().toSpanId()));
            logBean.setOperationName(span.getOperationName());
            logBean.setStartMicros(span.getStartTimestamp());
            logBean.setFinishMicros(finishMicros);
            logBean.setCostMs(((float) (finishMicros - span.getStartTimestamp())) / 1000f);
            logBean.setTags(getTags(span));
            logBean.setLogs(getLogs(span));
            logBean.setBaggage(TracerUtils.buildBaggage(span.context().baggageItems()));

            // 日志
            boolean error = (Boolean) NullSafe.of(span.getTags()).getOrDefault(Tags.ERROR.getKey(), false);
            if (error) {
                log.error("[Tracer] {}", JsonUtils.toJson(logBean));
            } else {
                log.info("[Tracer] {}", JsonUtils.toJson(logBean));
            }

        } catch (Throwable ex) {
            log.error("Finish span error", ex);
        }
    }

    private Map<String, Object> getTags(SpanImpl span) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> tags = span.getTags();
        if (tags != null) {
            for (Map.Entry<String, Object> entry : tags.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

    private TreeMap<Long, Object> getLogs(SpanImpl span) {
        TreeMap<Long, Object> result = new TreeMap<>();

        TreeMap<Long, String> eventLogs = span.getEventLogs();
        TreeMap<Long, Map<String, ?>> kvLogs = span.getKvLogs();

        if (eventLogs != null) {
            for (Map.Entry<Long, String> entry : eventLogs.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (kvLogs != null) {
            for (Map.Entry<Long, Map<String, ?>> entry : kvLogs.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

    @Data
    static class LogBean {

        private String operationName;
        private float costMs;
        private String traceId;
        private String spanId;
        private String parentSpanId;
        private List<String> followSpanIds;
        private long startMicros;
        private long finishMicros;
        private Map<String, Object> tags;
        private TreeMap<Long, Object> logs;
        private Map<String, String> baggage;

    }

}
