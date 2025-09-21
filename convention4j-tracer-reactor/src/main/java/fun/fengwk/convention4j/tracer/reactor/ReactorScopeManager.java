package fun.fengwk.convention4j.tracer.reactor;

import fun.fengwk.convention4j.tracer.scope.ConventionScopeManager;
import fun.fengwk.convention4j.tracer.scope.TtlScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import reactor.util.context.ContextView;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author fengwk
 */
@Slf4j
public class ReactorScopeManager implements ConventionScopeManager {

    private final TtlScopeManager ttlScopeManager;

    public ReactorScopeManager(TtlScopeManager ttlScopeManager) {
        this.ttlScopeManager = Objects.requireNonNull(ttlScopeManager, "ttlScopeManager must not be null");
    }

    @Override
    public Scope activate(Span span) {
        ContextView ctxView = ReactorTracerUtils.getCurrentContextView();
        if (ctxView == null) {
            // 非flux环境下使用ttlScopeManager
            return ttlScopeManager.activate(span);
        }
        ConcurrentLinkedDeque<Span> spanStack = ReactorTracerUtils.getSpanStack(ctxView);
        if (spanStack == null) {
            log.error("flux trace not enable, span: {}", span);
            throw new IllegalStateException("flux trace not enable");
        }
        if (!spanStack.contains(span)) {
            spanStack.addFirst(span);
            ReactorTracerUtils.keepSpanStackMaxSize(spanStack);
        }
        return new ReactorScope(spanStack, span);
    }

    @Override
    public Span activeSpan() {
        ContextView ctxView = ReactorTracerUtils.getCurrentContextView();
        if (ctxView == null) {
            // 非flux环境下使用ttlScopeManager
            return ttlScopeManager.activeSpan();
        }
        return ReactorTracerUtils.activeSpan(ctxView);
    }

    @Override
    public void close() {
        // nothing to do
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
            if (!spanStack.remove(span)) {
                log.warn("Reactor scope failed to remove span, no matching span was found, span: {}", span);
            }
        }

    }

}
