package fun.fengwk.convention4j.tracer.reactor;

import fun.fengwk.convention4j.common.function.Func0T1;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.reactor.aspect.ReactorAspect;
import fun.fengwk.convention4j.tracer.scope.TtlScopeManager;
import fun.fengwk.convention4j.tracer.scope.aspect.MDCThreadScopeAspect;
import fun.fengwk.convention4j.tracer.scope.aspect.ThreadScopeAspect;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;

/**
 * ReactorTracer工具
 *
 * @author fengwk
 */
@Slf4j
public class ReactorTracerUtils {

    private static final String ASPECT_NAME = "convention_reactor_tracer";

    private static final String SPAN_STACK = ReactorTracerUtils.class.getName() + ".SPAN_STACK";

    private static ThreadLocal<ContextView> CONTEXT_VIEW_TL = new ThreadLocal<>();

    private static final int MAX_STACK_SIZE = 10000;

    private ReactorTracerUtils() {
    }

    static ContextView setContextViewTL(ContextView contextView) {
        ContextView store = CONTEXT_VIEW_TL.get();
        if (contextView != null) {
            CONTEXT_VIEW_TL.set(contextView);
        } else {
            CONTEXT_VIEW_TL.remove();
        }
        return store;
    }

    static void removeContextViewTL(ContextView store) {
        if (store == null) {
            CONTEXT_VIEW_TL.remove();
        } else {
            CONTEXT_VIEW_TL.set(store);
        }
    }

    static ContextView getCurrentContextView() {
        return CONTEXT_VIEW_TL.get();
    }

    /**
     * 初始化全局的 ReactorTracer，必须在最开始时调用此方法
     */
    public static void initializeGlobalTracer() {
        ThreadScopeAspect threadScopeAspect = new MDCThreadScopeAspect();
        ScopeManager scopeManager = new ReactorScopeManager(new TtlScopeManager(threadScopeAspect));
        Slf4jSpanFinisher finisher = new Slf4jSpanFinisher();
        Tracer tracer = TracerUtils.buildTracer(scopeManager, finisher);
        ReactorAspect.registerAspect(ASPECT_NAME, new ReactorTracerSubscriberAspect(tracer, threadScopeAspect));
        GlobalTracer.registerIfAbsent(tracer);
    }

    /**
     * 获取当前活跃的Span Mono
     *
     * @return 当前活跃的Span Mono
     */
    public static Mono<Optional<Span>> activeSpan() {
        return Mono.deferContextual(ctxView -> {
            Span span = activeSpan(ctxView);
            return Mono.just(Optional.ofNullable(span));
        });
    }

    /**
     * 获取当前活跃的Span
     *
     * @param contextView 上下文视图
     * @return 当前活跃的Span
     */
    public static Span activeSpan(ContextView contextView) {
        ConcurrentLinkedDeque<Span> spanStack = getSpanStack(contextView);
        if (spanStack == null || spanStack.isEmpty()) {
            return null;
        }
        return spanStack.getFirst();
    }

    /**
     * 支持Reactor的版本
     *
     * @see TracerUtils#executeAndReturn(Func0T1, SpanInfo)
     */
    public static <R, T extends Throwable> R executeAndReturn(
        Tracer tracer, SpanInfo spanInfo, Func0T1<R, T> executor, Class<R> returnType) throws T {
        if (returnType == Mono.class) {
            Mono<?> mono = (Mono<?>) executor.apply();
            return (R) ReactorTracerUtils.newSpan(tracer, spanInfo, mono);
        } else if (returnType == Flux.class) {
            Flux<?> flux = (Flux<?>) executor.apply();
            return (R) ReactorTracerUtils.newSpan(tracer, spanInfo, flux);
        } else {
            return TracerUtils.executeAndReturn(tracer, spanInfo, executor);
        }
    }

    /**
     * 新开一个span
     *
     * @param tracer   Tracer
     * @param spanInfo SpanInfo
     * @param mono     Mono
     * @param <T>      T
     * @return Mono
     */
    public static <T> Mono<T> newSpan(Tracer tracer, SpanInfo spanInfo, Mono<T> mono) {
        // 解决从普通代码切换到Flux代码的场景
        Span span = tracer.activeSpan();
        return newSpan(tracer, spanInfo, mono, span == null ? null : span.context());
    }

    /**
     * 新开一个span
     *
     * @param tracer   Tracer
     * @param spanInfo SpanInfo
     * @param mono     Mono
     * @param parent   SpanContext
     * @param <T>      T
     * @return Mono
     */
    public static <T> Mono<T> newSpan(Tracer tracer, SpanInfo spanInfo, Mono<T> mono, SpanContext parent) {
        return Mono.deferContextual(startSpan(tracer, spanInfo, parent))
            .flatMap(spanOpt -> {
                if (spanOpt.isEmpty()) {
                    return mono;
                } else {
                    Span span = spanOpt.get();
                    return Mono.deferContextual(ctxView -> mono
                        .doOnError(ex -> {
                            span.setTag(Tags.ERROR, true);
                            span.log(ex.getMessage());
                        })
                        .doOnCancel(() -> finishSpan(span, ctxView))
                        .doOnTerminate(() -> finishSpan(span, ctxView)));
                }
            })
            .contextWrite(ReactorTracerUtils::enableTrace);
    }

    /**
     * 新开一个span
     *
     * @param tracer   Tracer
     * @param spanInfo SpanInfo
     * @param flux     Flux
     * @param <T>      T
     * @return Flux
     */
    public static <T> Flux<T> newSpan(Tracer tracer, SpanInfo spanInfo, Flux<T> flux) {
        // 解决从普通代码切换到Flux代码的场景
        Span span = tracer.activeSpan();
        return newSpan(tracer, spanInfo, flux, span == null ? null : span.context());
    }

    /**
     * 新开一个span
     *
     * @param tracer   Tracer
     * @param spanInfo SpanInfo
     * @param flux     Flux
     * @param parent   SpanContext
     * @param <T>      T
     * @return Flux
     */
    public static <T> Flux<T> newSpan(Tracer tracer, SpanInfo spanInfo, Flux<T> flux, SpanContext parent) {
        return Flux.deferContextual(startSpan(tracer, spanInfo, parent))
            .flatMap(spanOpt -> {
                if (spanOpt.isEmpty()) {
                    return flux;
                } else {
                    Span span = spanOpt.get();
                    return Flux.deferContextual(ctxView -> flux
                        .doOnError(ex -> {
                            span.setTag(Tags.ERROR, true);
                            span.log(ex.getMessage());
                        })
                        .doOnCancel(() -> finishSpan(span, ctxView))
                        .doOnTerminate(() -> finishSpan(span, ctxView)));
                }
            })
            .contextWrite(ReactorTracerUtils::enableTrace);
    }

    /**
     * 启用追踪
     *
     * @return 上下文映射函数
     */
    static Context enableTrace(Context ctx) {
        ConcurrentLinkedDeque<Span> spanStack = getSpanStack(ctx);
        if (spanStack == null) {
            spanStack = new ConcurrentLinkedDeque<>();
            ctx = ctx.put(SPAN_STACK, spanStack);
        }
        return ctx;
    }

    /**
     * 启用Span
     *
     * @param spanInfo span信息
     * @return 上下文映射函数
     */
    private static Function<ContextView, Mono<Optional<Span>>> startSpan(Tracer tracer, SpanInfo spanInfo, SpanContext parent) {

        return ctxView -> {
            ConcurrentLinkedDeque<Span> spanStack = getSpanStack(ctxView);
            if (spanStack == null) {
                log.error("flux trace not enable, spanInfo: {}", spanInfo);
                return Mono.error(new IllegalStateException("flux trace not enable"));
            }

            SpanContext p = parent;
            if (p == null && !spanStack.isEmpty()) {
                Span activeSpan = spanStack.getFirst();
                p = NullSafe.map(activeSpan, Span::context);
            }

            Span span = TracerUtils.startSpan(tracer, spanInfo, p);
            if (span != null) {
                spanStack.addFirst(span);
                keepSpanStackMaxSize(spanStack);
            }
            return Mono.just(Optional.ofNullable(span));
        };
    }

    /**
     * 结束Span
     */
    private static void finishSpan(Span span, ContextView contextView) {
        ConcurrentLinkedDeque<Span> spanStack = getSpanStack(contextView);
        if (spanStack == null) {
            return;
        }
        spanStack.remove(span);
        span.finish();
    }

    static ConcurrentLinkedDeque<Span> getSpanStack(ContextView contextView) {
        if (contextView != null && contextView.hasKey(SPAN_STACK)) {
            return contextView.get(SPAN_STACK);
        }
        return null;
    }

    static void keepSpanStackMaxSize(ConcurrentLinkedDeque<Span> spanStack) {
        // 防止编程错误导致的内存泄露
        if (spanStack.size() > MAX_STACK_SIZE) {
            log.error("Span stack size exceeds max stack size: {}", MAX_STACK_SIZE);
            spanStack.removeLast();
        }
    }

}
