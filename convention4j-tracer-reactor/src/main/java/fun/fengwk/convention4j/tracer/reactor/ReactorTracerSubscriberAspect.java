package fun.fengwk.convention4j.tracer.reactor;

import fun.fengwk.convention4j.tracer.reactor.aspect.SubscriberAspect;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import lombok.Data;
import org.reactivestreams.Subscription;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author fengwk
 */
public class ReactorTracerSubscriberAspect implements SubscriberAspect {

    @Data
    static class StoreContext {

        private final ContextView storeContextView;
        private final Map<String, String> storeMdc;

    }

    private Object onInit(ContextView ctx) {
        ContextView storeContextView = ReactorTracerUtils.setContextViewTL(ctx);
        Span span = ReactorTracerUtils.activeSpan(ctx);
        Map<String, String> storeMdc = null;
        if (span != null) {
            storeMdc = TracerUtils.setMDC(span.context());
        }
        return new StoreContext(storeContextView, storeMdc);
    }

    private void onFinally(Object localContext) {
        StoreContext storeContext = (StoreContext) localContext;
        if (storeContext == null) {
            return;
        }
        if (storeContext.getStoreMdc() != null) {
            TracerUtils.clearMDC(storeContext.getStoreMdc());
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

        Span span = GlobalTracer.get().activeSpan();
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
