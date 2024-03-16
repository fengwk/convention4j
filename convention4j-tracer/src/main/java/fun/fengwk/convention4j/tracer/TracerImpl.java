package fun.fengwk.convention4j.tracer;

import fun.fengwk.convention4j.common.clock.Clock;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.finisher.SpanFinisher;
import fun.fengwk.convention4j.tracer.propagation.TracerTransformer;
import fun.fengwk.convention4j.tracer.tag.NumberTag;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.*;
import io.opentracing.propagation.Format;
import io.opentracing.tag.BooleanTag;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class TracerImpl implements Tracer {

    private final Clock clock;
    private final ScopeManager scopeManager;
    private final TracerTransformer tracerTransformer;
    private final SpanFinisher spanFinisher;

    public TracerImpl(Clock clock, ScopeManager scopeManager, TracerTransformer tracerTransformer, SpanFinisher spanFinisher) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
        this.scopeManager = Objects.requireNonNull(scopeManager, "Scope manager must not be null");
        this.tracerTransformer = Objects.requireNonNull(tracerTransformer, "Tracer transformer must not be null");
        this.spanFinisher = Objects.requireNonNull(spanFinisher, "Span finisher must not be null");
    }

    @Override
    public ScopeManager scopeManager() {
        return scopeManager;
    }

    @Override
    public Span activeSpan() {
        return scopeManager().activeSpan();
    }

    @Override
    public Scope activateSpan(Span span) {
        return scopeManager().activate(span);
    }

    @Override
    public SpanBuilder buildSpan(String operationName) {
        return new SpanBuilderImpl(operationName);
    }

    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        tracerTransformer.inject(spanContext, format, carrier);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        return tracerTransformer.extract(format, carrier);
    }

    @Override
    public void close() {
        if (scopeManager instanceof Closeable) {
            try {
                ((Closeable) scopeManager).close();
            } catch (Exception ex) {
                log.error("Close {} error", getClass().getSimpleName(), ex);
            }
        }
    }

    class SpanBuilderImpl implements SpanBuilder {

        private final String operationName;
        private Long startTimestamp;
        private List<TagValue> tagValues;
        private List<Reference> references;
        private boolean ignoreActiveSpan;

        SpanBuilderImpl(String operationName) {
            this.operationName = operationName;
        }

        @Override
        public SpanBuilder asChildOf(SpanContext parent) {
            addReference(References.CHILD_OF, parent);
            return this;
        }

        @Override
        public SpanBuilder asChildOf(Span parent) {
            addReference(References.CHILD_OF, parent.context());
            return this;
        }

        @Override
        public SpanBuilder addReference(String referenceType, SpanContext referencedContext) {
            if (references == null) {
                this.references = new ArrayList<>();
            }
            references.add(new Reference(referenceType, referencedContext));
            return this;
        }

        @Override
        public SpanBuilder ignoreActiveSpan() {
            this.ignoreActiveSpan = true;
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, String value) {
            StringTag stringTag = new StringTag(key);
            return withTag(stringTag, value);
        }

        @Override
        public SpanBuilder withTag(String key, boolean value) {
            BooleanTag booleanTag = new BooleanTag(key);
            return withTag(booleanTag, value);
        }

        @Override
        public SpanBuilder withTag(String key, Number value) {
            NumberTag numberTag = new NumberTag(key);
            return withTag(numberTag, value);
        }

        @Override
        public <T> SpanBuilder withTag(Tag<T> tag, T value) {
            if (tagValues == null) {
                this.tagValues = new ArrayList<>();
            }
            TagValue<T> tagValue = new TagValue<>(tag, value);
            tagValues.add(tagValue);
            return this;
        }

        @Override
        public SpanBuilder withStartTimestamp(long microseconds) {
            this.startTimestamp = microseconds;
            return this;
        }

        @Override
        public Span start() {
            if (startTimestamp == null) {
                this.startTimestamp = clock.currentTimeMicros();
            }

            SpanContext parentContext = null;
            SpanContextImpl spanContext;

            // the Tracer's ScopeManager.activeSpan() is not null,
            // and no explicit references are added via addReference,
            // and ignoreActiveSpan() is not invoked,
            if (activeSpan() != null && references == null && !ignoreActiveSpan) {
                // 使用父上下文的traceId
                parentContext = activeSpan().context();
                spanContext = SpanContextImpl.start(parentContext.toTraceId(), parentContext.baggageItems());
                addReference(References.CHILD_OF, parentContext);
            } else {
                if (references != null && !references.isEmpty()) {
                    // 使用references构建
                    // 首先查找CHILD_OF引用的上下文，如果不存在则使用任意一个引用的上下文
                    parentContext = NullSafe.map(TracerUtils.getChildOfReference(references), Reference::getSpanContext);
                    if (parentContext != null) {
                        spanContext = SpanContextImpl.start(parentContext.toTraceId(), parentContext.baggageItems());
                    } else {
                        SpanContext followedContext = references.get(0).getSpanContext();
                        spanContext = SpanContextImpl.start(followedContext.toTraceId(), followedContext.baggageItems());
                    }
                } else {
                    // 创建一个全新的上下文
                    spanContext = SpanContextImpl.start(null, null);
                }
            }

            SpanImpl span = new SpanImpl(clock, spanContext, spanFinisher, operationName, startTimestamp, references);

            // 根span初始化
            if (parentContext == null || StringUtils.isBlank(parentContext.toSpanId())) {
                TracerUtils.initializeRootSpan(span);
            }

            // 默认的span初始化
            TracerUtils.initializeSpan(span);

            // 处理tagValues
            if (tagValues != null) {
                tagValues.forEach(tagValue -> span.setTag(tagValue.getTag(), tagValue.getValue()));
            }

            return span;
        }

    }

}
