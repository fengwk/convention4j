package fun.fengwk.convention4j.tracer.propagation;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.SpanContextImpl;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
@Data
public class SpanContextBean {

    private String traceId;
    private String spanId;
    private Map<String, String> baggage;

    public static SpanContextBean createFromSpanContext(SpanContext spanContext) {
        if (spanContext == null) {
            return null;
        }
        SpanContextBean spanContextBean = new SpanContextBean();
        spanContextBean.setTraceId(spanContext.toTraceId());
        spanContextBean.setSpanId(spanContext.toSpanId());
        spanContextBean.setBaggage(TracerUtils.buildBaggage(spanContext.baggageItems()));
        return spanContextBean;
    }

    public static SpanContextBean deserialize(String serialized) {
        if (StringUtils.isBlank(serialized)) {
            return null;
        }
        SpanContextBean spanContextBean = null;
        try {
            spanContextBean = JsonUtils.fromJson(serialized, SpanContextBean.class);
        } catch (Exception ex) {
            log.error("Deserialize span context bean error, serialized: {}", serialized, ex);
        }
        return spanContextBean;
    }

    public String serialize() {
        String serialized = null;
        try {
            serialized = JsonUtils.toJson(this);
        } catch (Exception ex) {
            log.error("Serialize span context bean error, this: {}", this, ex);
        }
        return serialized;
    }

    public SpanContext toSpanContext() {
        if (StringUtils.isBlank(getTraceId())) {
            return null;
        }
        return new SpanContextImpl(getTraceId(), getSpanId(), getBaggage());
    }

}
