package fun.fengwk.convention4j.tracer.reactor;

import fun.fengwk.convention4j.tracer.reactor.aspect.SubscriberAspect;
import fun.fengwk.convention4j.tracer.scope.aspect.EmptyThreadScopeAspect;
import fun.fengwk.convention4j.tracer.scope.aspect.ThreadScopeAspect;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.Data;
import org.reactivestreams.Subscription;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author fengwk
 */
public class ReactorTracerSubscriberAspect implements SubscriberAspect {

    private final Tracer tracer;
    private final ThreadScopeAspect threadScopeAspect;

    public ReactorTracerSubscriberAspect(Tracer tracer, ThreadScopeAspect threadScopeAspect) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.threadScopeAspect = threadScopeAspect == null ? new EmptyThreadScopeAspect() : threadScopeAspect;
    }

    @Data
    static class StoreContext {

        private final ContextView storeContextView;
        private final Map<String, String> store;

    }

    private Object onInit(ContextView ctx) {
        ContextView storeContextView = ReactorTracerUtils.setContextViewTL(ctx);
        Span span = ReactorTracerUtils.activeSpan(ctx);
        Map<String, String> store = null;
        if (span != null) {
            store = threadScopeAspect.onEnter(span.context());
        }
        return new StoreContext(storeContextView, store);
    }

    private void onFinally(Object localContext) {
        StoreContext storeContext = (StoreContext) localContext;
        if (storeContext == null) {
            return;
        }
        if (storeContext.getStore() != null) {
            threadScopeAspect.onExit(storeContext.getStore());
        }
        ReactorTracerUtils.removeContextViewTL(storeContext.getStoreContextView());
    }

    @Override
    public Context writeContext(Context context) {
        // 非reactor首次进入reactor时如果有span需要设置到reactor作用域中

        Span reactorSpan = ReactorTracerUtils.activeSpan(context);
        if (reactorSpan != null) {
            return context;
        }

        Span span = tracer.activeSpan();
        if (span == null) {
            return context;
        }

        context = ReactorTracerUtils.enableTrace(context);
        ConcurrentLinkedDeque<Span> spanStack = ReactorTracerUtils.getSpanStack(context);
        spanStack.addFirst(span);
        ReactorTracerUtils.keepSpanStackMaxSize(spanStack);
        return context;
    }

    @Override
    public Object subscribeInit(ContextView ctx) {
        return onInit(ctx);
    }

    @Override
    public void subscribeFinally(ContextView ctx, Object subscribeContext) {
        onFinally(subscribeContext);
    }

    @Override
    public Object onSubscribeInit(Subscription s, ContextView ctx) {
        return onInit(ctx);
    }

    @Override
    public void onSubscribeFinally(Subscription s, ContextView ctx, Object onSubscribeContext) {
        onFinally(onSubscribeContext);
    }

    @Override
    public Object onNextInit(Object element, ContextView ctx) {
        return onInit(ctx);
    }

    @Override
    public void onNextFinally(Object element, ContextView ctx, Object onNextContext) {
        onFinally(onNextContext);
    }

    @Override
    public Object onErrorInit(Throwable t, ContextView ctx) {
        return onInit(ctx);
    }

    @Override
    public void onErrorFinally(Throwable t, ContextView ctx, Object onErrorContext) {
        onFinally(onErrorContext);
    }

    @Override
    public Object onCompleteInit(ContextView ctx) {
        return onInit(ctx);
    }

    @Override
    public void onCompleteFinally(ContextView ctx, Object onCompleteContext) {
        onFinally(onCompleteContext);
    }

}
