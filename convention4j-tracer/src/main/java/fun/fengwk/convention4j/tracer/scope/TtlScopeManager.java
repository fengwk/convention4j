package fun.fengwk.convention4j.tracer.scope;

import com.alibaba.ttl.TransmittableThreadLocal;
import fun.fengwk.convention4j.tracer.scope.aspect.ThreadScopeAspect;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class TtlScopeManager implements ConventionScopeManager {

    private static final int MAX_STACK_SIZE = 10000;
    private static final TransmittableThreadLocal<LinkedList<ScopeImpl>> TTL_SCOPE_STACK = new TransmittableThreadLocal<>() {
        @Override
        protected LinkedList<ScopeImpl> initialValue() {
            return new LinkedList<>();
        }

        @Override
        protected void beforeExecute() {
            // 使用自定义agent替代
            // super.beforeExecute();
            // LinkedList<ScopeImpl> scopeStack = get();
            // synchronized (scopeStack) {
            // if (!scopeStack.isEmpty()) {
            // ScopeImpl scope = scopeStack.getFirst();
            // TracerUtils.setMDC(scope.getSpan().context());
            // }
            // }
        }

        @Override
        protected void afterExecute() {
            // 使用自定义agent替代
            // super.afterExecute();
            // LinkedList<ScopeImpl> scopeStack = get();
            // synchronized (scopeStack) {
            // if (!scopeStack.isEmpty()) {
            // ScopeImpl scope = scopeStack.getFirst();
            // TracerUtils.clearMDC(scope.getSpan().context());
            // }
            // }
        }
    };

    private final ThreadScopeAspect threadScopeAspect;

    public TtlScopeManager(ThreadScopeAspect threadScopeAspect) {
        this.threadScopeAspect = Objects.requireNonNull(threadScopeAspect, "threadScopeAspect must not be null");
    }

    @Override
    public Scope activate(Span span) {
        long threadId = Thread.currentThread().getId();
        LinkedList<ScopeImpl> scopeStack = TTL_SCOPE_STACK.get();
        Map<String, String> store = threadScopeAspect.onEnter(span.context());
        ScopeImpl scope = new ScopeImpl(scopeStack, span, store, threadId, threadScopeAspect);
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
        // nothing to do
    }

    @EqualsAndHashCode
    @ToString
    static class ScopeImpl implements Scope {

        private final LinkedList<ScopeImpl> scopeStack;
        @Getter
        private final Span span;
        private final Map<String, String> store;
        private final long activateThreadId;
        private final ThreadScopeAspect threadScopeAspect;

        public ScopeImpl(LinkedList<ScopeImpl> scopeStack, Span span, Map<String, String> store,
                         long activateThreadId, ThreadScopeAspect threadScopeAspect) {
            this.scopeStack = scopeStack;
            this.span = span;
            this.store = store;
            this.activateThreadId = activateThreadId;
            this.threadScopeAspect = threadScopeAspect;
        }

        @Override
        public void close() {
            long threadId = Thread.currentThread().getId();
            if (!Objects.equals(threadId, activateThreadId)) {
                log.warn("The thread that closes scope is inconsistent with the activated thread,"
                        + " currentThreadId: {}, activateThreadId: {}", threadId, activateThreadId);
            }
            synchronized (scopeStack) {
                if (scopeStack.remove(this)) {
                    threadScopeAspect.onExit(store);
                }
            }
        }

    }

}
