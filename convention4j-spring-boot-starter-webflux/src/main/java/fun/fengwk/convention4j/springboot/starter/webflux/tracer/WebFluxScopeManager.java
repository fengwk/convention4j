package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.util.OrderedObject;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import fun.fengwk.convention4j.tracer.scope.ConventionScopeManager;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
@AutoService(ConventionScopeManager.class)
public class WebFluxScopeManager implements ConventionScopeManager {

    private static final int MAX_STACK_SIZE = 1000;

    private static final String FLUX_SCOPE_STACK_KEY = WebFluxScopeManager.class.getName() + "#FLUX_SCOPE_STACK_KEY";
    private static final ThreadLocal<WebFluxContext> WEB_FLUX_CONTEXT_TL = new ThreadLocal<>();

    public static WebFluxContext setWebFluxContext(WebFluxContext webFluxContext) {
        WebFluxContext old = WEB_FLUX_CONTEXT_TL.get();
        WEB_FLUX_CONTEXT_TL.set(webFluxContext);
        if (webFluxContext != null) {
            ConcurrentMap<String, Object> attributes = webFluxContext.getAttributes();
            LinkedList<FluxScope> scopeStack = (LinkedList<FluxScope>) attributes.get(FLUX_SCOPE_STACK_KEY);
            synchronized (scopeStack) {
                if (scopeStack != null && !scopeStack.isEmpty()) {
                    FluxScope currentScope = scopeStack.getFirst();
                    TracerUtils.setMDC(currentScope.getSpan().context());
                }
            }
        }
        return old;
    }

    public static void clearWebFluxContext() {
        WEB_FLUX_CONTEXT_TL.remove();
    }

    @Override
    public Scope activate(Span span) {
        if (!(span instanceof WebFluxSpan webFluxSpan)) {
            log.error("Span must be instance of {}", WebFluxSpan.class.getSimpleName());
            throw new IllegalArgumentException("Span must be instance of " + WebFluxSpan.class.getSimpleName());
        }

        WebFluxContext webFluxContext = webFluxSpan.getWebFluxContext();
        ConcurrentMap<String, Object> attributes = webFluxContext.getAttributes();
        LinkedList<FluxScope> scopeStack = (LinkedList<FluxScope>) attributes.computeIfAbsent(
            FLUX_SCOPE_STACK_KEY, k -> new LinkedList<>());
        Map<String, String> mdcStore = TracerUtils.setMDC(webFluxSpan.context());
        FluxScope fluxScope = new FluxScope(webFluxContext, webFluxSpan, mdcStore);

        synchronized (scopeStack) {
            // addFirst可以使最近添加的节点以最少的遍历数被移出LinkedList
            scopeStack.addFirst(fluxScope);
            // 防止编程错误导致的内存泄露
            if (scopeStack.size() > MAX_STACK_SIZE) {
                log.error("Scope stack size exceeds max stack size: {}", MAX_STACK_SIZE);
                FluxScope removedScope = scopeStack.removeLast();
                removedScope.close();
            }
        }
        return fluxScope;
    }

    @Override
    public Span activeSpan() {
        WebFluxContext webFluxContext = WEB_FLUX_CONTEXT_TL.get();
        if (webFluxContext != null) {
            ConcurrentMap<String, Object> attributes = webFluxContext.getAttributes();
            LinkedList<FluxScope> scopeStack = (LinkedList<FluxScope>) attributes.get(FLUX_SCOPE_STACK_KEY);
            synchronized (scopeStack) {
                return scopeStack.isEmpty() ? null : scopeStack.getFirst().getSpan();
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return OrderedObject.HIGHEST_PRECEDENCE;
    }

    @Override
    public void close() {
        // nothing to do
    }

    static class FluxScope implements Scope {

        @Getter
        private final WebFluxContext webFluxContext;
        @Getter
        private final Span span;
        private final Map<String, String> mdcStore;

        public FluxScope(WebFluxContext webFluxContext, Span span, Map<String, String> mdcStore) {
            this.webFluxContext = Objects.requireNonNull(webFluxContext, "WebFluxContext must not be null");
            this.span = Objects.requireNonNull(span, "Span must not be null");
            this.mdcStore = Objects.requireNonNull(mdcStore, "MDCStore must not be null");
        }

        @Override
        public void close() {
            LinkedList<Scope> scopeStack = (LinkedList<Scope>) webFluxContext.getAttributes().get(FLUX_SCOPE_STACK_KEY);
            if (scopeStack != null) {
                synchronized (scopeStack) {
                    if (scopeStack.remove(this)) {
                        TracerUtils.clearMDC(mdcStore);
                        if (scopeStack.isEmpty()) {
                            webFluxContext.getAttributes().remove(FLUX_SCOPE_STACK_KEY);
                        }
                    }
                }
            }
        }

    }

}
