package fun.fengwk.convention4j.tracer;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.clock.Clock;
import fun.fengwk.convention4j.tracer.finisher.SpanFinisher;
import fun.fengwk.convention4j.tracer.tag.NumberTag;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.BooleanTag;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class SpanImpl implements Span {

    private final Clock clock;
    private final SpanContextImpl spanContext;
    private final SpanFinisher spanFinisher;
    @Getter
    private String operationName;
    @Getter
    private final long startTimestamp;
    @Getter
    private final List<Reference> references;
    @Getter
    private TreeMap<Long, Map<String, ?>> kvLogs;
    @Getter
    private TreeMap<Long, String> eventLogs;
    @Getter
    private Map<String, Object> tags;

    public SpanImpl(Clock clock,
                    SpanContextImpl spanContext,
                    SpanFinisher spanFinisher,
                    String operationName,
                    long startTimestamp,
                    List<Reference> references) {
        this.clock = Objects.requireNonNull(clock, "Clock context must not be null");
        this.spanContext = Objects.requireNonNull(spanContext, "Span context must not be null");
        this.spanFinisher = Objects.requireNonNull(spanFinisher, "Span finisher must not be null");
        this.operationName = NullSafe.of(operationName, "unknown");
        this.references = NullSafe.of(references);
        if (startTimestamp <= 0) {
            throw new IllegalArgumentException("startTimestamp must be positive");
        }
        this.startTimestamp = startTimestamp;
    }

    @Override
    public SpanContext context() {
        return spanContext;
    }

    @Override
    public Span setTag(String key, String value) {
        StringTag stringTag = new StringTag(key);
        return setTag(stringTag, value);
    }

    @Override
    public Span setTag(String key, boolean value) {
        BooleanTag booleanTag = new BooleanTag(key);
        return setTag(booleanTag, value);
    }

    @Override
    public Span setTag(String key, Number value) {
        NumberTag numberTag = new NumberTag(key);
        return setTag(numberTag, value);
    }

    @Override
    public <T> Span setTag(Tag<T> tag, T value) {
        if (tags == null) {
            this.tags = new HashMap<>();
        }
        tags.put(tag.getKey(), value);
        return this;
    }

    @Override
    public Span log(Map<String, ?> fields) {
        return log(clock.currentTimeMicros(), fields);
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        if (kvLogs == null) {
            this.kvLogs = new TreeMap<>();
        }
        kvLogs.put(timestampMicroseconds, fields);
        return this;
    }

    @Override
    public Span log(String event) {
        return log(clock.currentTimeMicros(), event);
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        if (eventLogs == null) {
            this.eventLogs = new TreeMap<>();
        }
        eventLogs.put(timestampMicroseconds, event);
        return this;
    }

    @Override
    public Span setBaggageItem(String key, String value) {
        this.spanContext.setBaggageItem(key, value);
        return this;
    }

    @Override
    public String getBaggageItem(String key) {
        return this.spanContext.getBaggageItem(key);
    }

    @Override
    public Span setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    @Override
    public void finish() {
        finish(clock.currentTimeMicros());
    }

    @Override
    public void finish(long finishMicros) {
        spanFinisher.finish(this, finishMicros);
    }

}
