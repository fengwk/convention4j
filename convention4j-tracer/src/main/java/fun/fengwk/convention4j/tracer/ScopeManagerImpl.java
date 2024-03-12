package fun.fengwk.convention4j.tracer;

import com.alibaba.ttl.TransmittableThreadLocal;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fengwk
 */
@Slf4j
public class ScopeManagerImpl implements ScopeManager, AutoCloseable {

    private static final int MAX_STACK_SIZE = 1000;
    private static final TransmittableThreadLocal<LinkedList<ScopeImpl>> TTL_SCOPE_STACK = new TransmittableThreadLocal<>() {
        @Override
        protected LinkedList<ScopeImpl> initialValue() {
            return new LinkedList<>();
        }

        @Override
        protected void beforeExecute() {
            // 使用自定义agent替代
//            super.beforeExecute();
//            LinkedList<ScopeImpl> scopeStack = get();
//            synchronized (scopeStack) {
//                if (!scopeStack.isEmpty()) {
//                    ScopeImpl scope = scopeStack.getFirst();
//                    TracerUtils.setMDC(scope.getSpan().context());
//                }
//            }
        }

        @Override
        protected void afterExecute() {
            // 使用自定义agent替代
//            super.afterExecute();
//            LinkedList<ScopeImpl> scopeStack = get();
//            synchronized (scopeStack) {
//                if (!scopeStack.isEmpty()) {
//                    ScopeImpl scope = scopeStack.getFirst();
//                    TracerUtils.clearMDC(scope.getSpan().context());
//                }
//            }
        }
    };

    @Override
    public Scope activate(Span span) {
        LinkedList<ScopeImpl> scopeStack = TTL_SCOPE_STACK.get();
        ScopeImpl scope = new ScopeImpl(scopeStack, span);
        synchronized (scopeStack) {
            // addFirst可以使最近添加的节点以最少的遍历数被移出LinkedList
            scopeStack.addFirst(scope);
            // 防止编程错误导致的内存泄露
            if (scopeStack.size() > MAX_STACK_SIZE) {
                log.error("Scope stack size exceeds max stack size: {}", MAX_STACK_SIZE);
                ScopeImpl removedScope = scopeStack.removeLast();
                removedScope.close();
            }
        }
        // MDC支持
        TracerUtils.setMDC(span.context());
        return scope;
    }

    @Override
    public Span activeSpan() {
        LinkedList<ScopeImpl> scopeStack = TTL_SCOPE_STACK.get();
        synchronized (scopeStack) {
            return scopeStack.isEmpty() ? null : scopeStack.getFirst().getSpan();
        }
    }

    @Override
    public void close() {
        TTL_SCOPE_STACK.remove();
    }

    @EqualsAndHashCode
    @ToString
    public class ScopeImpl implements Scope {

        private final List<ScopeImpl> scopeStack;
        @Getter
        private final Span span;
        private final String trace;

        public ScopeImpl(List<ScopeImpl> scopeStack, Span span) {
            this.scopeStack = scopeStack;
            this.span = span;
            this.trace = buildTrace(span);
        }

        @Override
        public void close() {
            synchronized (scopeStack) {
                if (scopeStack.remove(this)) {
                    TracerUtils.clearMDC(span.context());
                }
            }
        }

        private String buildTrace(Span span) {
            TraceInfo traceInfo = new TraceInfo(span.context().toTraceId(), span.context().toSpanId());
            return JsonUtils.toJson(traceInfo);
        }

    }

    @Data
    static class TraceInfo {
        private final String traceId;
        private final String spanId;
    }

}
