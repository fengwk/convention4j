package fun.fengwk.convention4j.tracer.reactor;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.util.OrderedObject;
import fun.fengwk.convention4j.tracer.scope.ConventionScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author fengwk
 */
@Slf4j
@AutoService(ConventionScopeManager.class)
public class ReactorScopeManager implements ConventionScopeManager {

    @Override
    public Scope activate(Span span) {
        ConcurrentLinkedDeque<Span> spanStack = ReactorTracerUtils.getSpanStack(ReactorTracerUtils.getCurrentContextView());
        if (spanStack == null) {
            log.error("flux trace not enable, span: {}", span);
            throw new IllegalStateException("flux trace not enable");
        }
        if (!spanStack.contains(span)) {
            spanStack.addFirst(span);
        }
        return new ReactorScope(spanStack, span);
    }

    @Override
    public Span activeSpan() {
        return ReactorTracerUtils.activeSpan(ReactorTracerUtils.getCurrentContextView());
    }

    @Override
    public void close() {
        // nothing to do
    }

    /**
     * 优先使用ReactorScopeManager
     */
    @Override
    public int getOrder() {
        return OrderedObject.HIGHEST_PRECEDENCE;
    }

    @EqualsAndHashCode
    @ToString
    static class ReactorScope implements Scope {

        private final ConcurrentLinkedDeque<Span> spanStack;
        private final Span span;

        ReactorScope(ConcurrentLinkedDeque<Span> spanStack, Span span) {
            this.spanStack = Objects.requireNonNull(spanStack, "spanStack must not be null");
            this.span = Objects.requireNonNull(span, "span must not be null");
        }

        @Override
        public void close() {
            spanStack.remove(span);
        }

    }

}
