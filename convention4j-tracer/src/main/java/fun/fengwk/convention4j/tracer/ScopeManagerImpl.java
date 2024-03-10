package fun.fengwk.convention4j.tracer;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fengwk
 */
@Slf4j
public class ScopeManagerImpl implements ScopeManager, AutoCloseable {

    private static final int MAX_STACK_SIZE = 1000;
    private static final TransmittableThreadLocal<List<ScopeImpl>> TTL_SCOPE_STACK
        = TransmittableThreadLocal.withInitial(() -> Collections.synchronizedList(new LinkedList<>()));

    @Override
    public Scope activate(Span span) {
        List<ScopeImpl> scopeStack = TTL_SCOPE_STACK.get();
        ScopeImpl scope = new ScopeImpl(scopeStack, span);
        // addFirst可以使最近添加的节点以最少的遍历数被移出LinkedList
        scopeStack.add(0, scope);
        // 防止编程错误导致的内存泄露
        if (scopeStack.size() > MAX_STACK_SIZE) {
            log.error("Scope stack size exceeds max stack size: {}", MAX_STACK_SIZE);
            ScopeImpl removedScope = scopeStack.remove(scopeStack.size() - 1);
            removedScope.close();
        }
        return scope;
    }

    @Override
    public Span activeSpan() {
        List<ScopeImpl> scopeStack = TTL_SCOPE_STACK.get();
        return scopeStack.isEmpty() ? null : scopeStack.get(0).getSpan();
    }

    @Override
    public void close() {
        TTL_SCOPE_STACK.remove();
    }

}
