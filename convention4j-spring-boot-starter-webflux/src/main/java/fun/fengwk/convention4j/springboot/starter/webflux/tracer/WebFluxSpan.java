package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
public class WebFluxSpan implements Span {

    private final Span delegate;
    @Getter
    private final WebFluxContext webFluxContext;

    public WebFluxSpan(Span delegate, WebFluxContext webFluxContext) {
        this.delegate = Objects.requireNonNull(delegate, "Span delegate must not be null");
        this.webFluxContext = Objects.requireNonNull(webFluxContext, "WebFluxContext must not be null");
    }

    @Override
    public SpanContext context() {
        return delegate.context();
    }

    @Override
    public Span setTag(String key, String value) {
        return delegate.setTag(key, value);
    }

    @Override
    public Span setTag(String key, boolean value) {
        return delegate.setTag(key, value);
    }

    @Override
    public Span setTag(String key, Number value) {
        return delegate.setTag(key, value);
    }

    @Override
    public <T> Span setTag(Tag<T> tag, T value) {
        return delegate.setTag(tag, value);
    }

    @Override
    public Span log(Map<String, ?> fields) {
        return delegate.log(fields);
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        return delegate.log(timestampMicroseconds, fields);
    }

    @Override
    public Span log(String event) {
        return delegate.log(event);
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        return delegate.log(timestampMicroseconds, event);
    }

    @Override
    public Span setBaggageItem(String key, String value) {
        return delegate.setBaggageItem(key, value);
    }

    @Override
    public String getBaggageItem(String key) {
        return delegate.getBaggageItem(key);
    }

    @Override
    public Span setOperationName(String operationName) {
        return delegate.setOperationName(operationName);
    }

    @Override
    public void finish() {
        delegate.finish();
    }

    @Override
    public void finish(long finishMicros) {
        delegate.finish(finishMicros);
    }

}
