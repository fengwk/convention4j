package fun.fengwk.convention4j.springboot.starter.webflux.context;

import fun.fengwk.convention4j.springboot.starter.webflux.tracer.WebFluxScopeManager;
import fun.fengwk.convention4j.springboot.starter.webflux.tracer.WebFluxSpan;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @author fengwk
 */
@Slf4j
public class WebFluxTracerContext {

    @Getter
    private final WebFluxContext webFluxContext;

    public WebFluxTracerContext(WebFluxContext webFluxContext) {
        this.webFluxContext = webFluxContext;
    }

    /**
     * 获取当前上下文
     *
     * @return 返回WebFluxTracerContext Mono
     */
    public static Mono<WebFluxTracerContext> get() {
        return WebFluxContext.get().map(opt -> {
            WebFluxContext webFluxContext = opt.orElse(null);
            return new WebFluxTracerContext(webFluxContext);
        });
    }

    /**
     * 在tracer上下文中执行
     *
     * @return 返回WebFluxContext
     */
    public <T> T execute(Supplier<T> executor) {
        WebFluxContext old = null;
        if (webFluxContext != null) {
            old = WebFluxScopeManager.setWebFluxContext(webFluxContext);
        }
        try {
            return executor.get();
        } finally {
            if (webFluxContext != null) {
                WebFluxScopeManager.clearWebFluxContext();
                if (old != null) {
                    WebFluxScopeManager.setWebFluxContext(old);
                }
            }
        }
    }

    /**
     * 激活一个新的Span
     *
     * @param spanInfo Span信息
     * @return 返回激活的Scope
     */
    public TraceInfo activate(SpanInfo spanInfo) {
        if (webFluxContext == null) {
            return null;
        }

        Span span = TracerUtils.startSpan(spanInfo);
        if (span == null) {
            return null;
        }

        WebFluxSpan webFluxSpan = new WebFluxSpan(span, webFluxContext);
        Scope scope = GlobalTracer.get().activateSpan(webFluxSpan);
        return new TraceInfo(webFluxSpan, scope);
    }

    /**
     * 结束当前Span
     *
     * @param traceInfo traceInfo
     */
    public void finish(TraceInfo traceInfo) {
        if (traceInfo == null) {
            return;
        }
        WebFluxSpan webFluxSpan = traceInfo.getWebFluxSpan();
        Scope scope = traceInfo.getScope();
        try {
            scope.close();
        } catch (Exception ex) {
            log.error("Close scope error", ex);
        }
        webFluxSpan.finish();
    }

}
