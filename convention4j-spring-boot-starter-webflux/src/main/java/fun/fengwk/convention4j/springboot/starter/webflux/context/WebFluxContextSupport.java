package fun.fengwk.convention4j.springboot.starter.webflux.context;

import fun.fengwk.convention4j.springboot.starter.webflux.tracer.ServerWebExchangeExtract;
import fun.fengwk.convention4j.springboot.starter.webflux.tracer.WebFluxSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.function.Function;

/**
 * 使用方法如下 {@code
 *  chain.filter(exchange).contextWrite(contextModifier(exchange)).then(finisher(exchange));
 * }
 *
 * @author fengwk
 */
@Slf4j
public abstract class WebFluxContextSupport {

    private static final String TRACE_CONTEXT_KEY = WebFluxContextSupport.class.getName() + "#TRACE_CONTEXT_KEY";

    protected Function<Context, Context> contextModifier(ServerWebExchange exchange) {
        return ctx -> {
            WebFluxContext webFluxContext = new WebFluxContext(exchange.getRequest(), exchange.getResponse());
            Context context = WebFluxContext.set(ctx, webFluxContext);
            Span span = startSpan(exchange);
            WebFluxSpan fluxSpan = new WebFluxSpan(span, webFluxContext);
            Scope scope = GlobalTracer.get().activateSpan(fluxSpan);
            exchange.getAttributes().put(TRACE_CONTEXT_KEY, new TraceInfo(fluxSpan, scope));
            return context;
        };
    }

    protected Mono<Void> finisher(ServerWebExchange exchange) {
        return Mono.fromRunnable(() -> {
            TraceInfo traceContext = exchange.getAttribute(TRACE_CONTEXT_KEY);
            finish(traceContext, exchange);
        });
    }

    private Span startSpan(ServerWebExchange exchange) {
        Tracer tracer = GlobalTracer.get();
        // 获取父级上下文
        SpanContext parentContext = tracer.extract(ServerWebExchangeExtract.FORMAT, exchange);
        // 构建span
        ServerHttpRequest request = exchange.getRequest();
        String relativePath = RequestPathUtils.extractPath(
            request.getPath().pathWithinApplication(), "/**");
        String operationName = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName)
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_SERVER)
            .withTag(Tags.HTTP_METHOD, request.getMethod().name())
            .withTag(Tags.HTTP_URL, request.getURI().toString());
        if (parentContext != null) {
            spanBuilder.asChildOf(parentContext);
        }
        // 开启span
        return spanBuilder.start();
    }

    private void finish(TraceInfo traceContext, ServerWebExchange exchange) {
        if (traceContext == null) {
            return;
        }

        WebFluxSpan webFluxSpan = traceContext.getWebFluxSpan();
        Scope scope = traceContext.getScope();

        ServerHttpResponse response = exchange.getResponse();
        int status = 500;
        if (response.getStatusCode() != null) {
            status = response.getStatusCode().value();
        }
        if (status < 200 || status >= 300) {
            webFluxSpan.setTag(Tags.ERROR, true);
        } else {
            webFluxSpan.setTag(Tags.ERROR, false);
        }
        webFluxSpan.setTag(Tags.HTTP_STATUS, status);
        try {
            scope.close();
        } catch (Exception ex) {
            log.error("Close scope error", ex);
        }
        webFluxSpan.finish();
    }

}
