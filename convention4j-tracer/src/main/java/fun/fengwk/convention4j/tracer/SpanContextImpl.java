package fun.fengwk.convention4j.tracer;

import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.SpanContext;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class SpanContextImpl implements SpanContext {

    private final String traceId;
    private final String spanId;
    private final Map<String, String> baggage;

    public SpanContextImpl(String traceId, String spanId, Map<String, String> baggage) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.baggage = NullSafe.of(baggage);
    }

    public static SpanContextImpl start(String traceId, Iterable<Map.Entry<String, String>> baggageIt) {
        return new SpanContextImpl(
            NullSafe.of(traceId, TracerUtils.generateTraceId()),
            TracerUtils.generateSpanId(),
            TracerUtils.buildBaggage(baggageIt));
    }

    public void setBaggageItem(String key, String value) {
        this.baggage.put(key, value);
    }

    public String getBaggageItem(String key) {
        return this.baggage.get(key);
    }

    @Override
    public String toTraceId() {
        return traceId;
    }

    @Override
    public String toSpanId() {
        return spanId;
    }

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return baggage.entrySet();
    }

}
