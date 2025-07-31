package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import fun.fengwk.convention4j.common.http.HttpUtils;
import fun.fengwk.convention4j.springboot.starter.webflux.context.RequestPathUtils;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.SpanPropagation;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * webflux处理器的过滤器，用于注入上下文
 *
 * @author fengwk
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxTracerFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        Mono<Void> chainFilterMono = ReactorTracerUtils.activeSpan()
            .flatMap(spanOpt -> {
                Span span = spanOpt.orElse(null);
                if (span != null) {
                    span.setTag(Tags.SPAN_KIND, Tags.SPAN_KIND_SERVER);
                    span.setTag(Tags.HTTP_METHOD, request.getMethod().name());
                    span.setTag(Tags.HTTP_URL, request.getURI().toString());
                }

                return chain.filter(exchange)
                    .doOnError(err -> {
                        if (span != null) {
                            if (err instanceof ResponseStatusException statusEx) {
                                span.setTag(Tags.HTTP_STATUS, statusEx.getStatusCode().value());
                            }
                            setResponseToSpan(span, exchange);
                            span.setTag(Tags.ERROR, true);
                            span.log(err.getMessage());
                        }
                    });
            });

        // 构建span信息
        String relativePath = RequestPathUtils.extractPath(
            request.getPath().pathWithinApplication(), "/**");
        String operationName = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        SpanInfo spanInfo = SpanInfo.builder()
            .operationName(operationName)
            .propagation(SpanPropagation.REQUIRED)
            .kind(Tags.SPAN_KIND_SERVER)
            .build();

        // 获取父级上下文
        SpanContext parentContext = GlobalTracer.get().extract(ServerWebExchangeExtract.FORMAT, exchange);

        // 启用新的span
        return ReactorTracerUtils.newSpan(chainFilterMono, spanInfo, parentContext);
    }

    private void setResponseToSpan(Span span, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        int status = 500;
        if (response.getStatusCode() != null) {
            status = response.getStatusCode().value();
        }
        if (HttpUtils.is2xx(status)) {
            span.setTag(Tags.ERROR, false);
        } else {
            span.setTag(Tags.ERROR, true);
        }
        span.setTag(Tags.HTTP_STATUS, status);
    }

}
