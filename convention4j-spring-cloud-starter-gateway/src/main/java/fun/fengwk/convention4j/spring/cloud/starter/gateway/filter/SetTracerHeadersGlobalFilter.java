package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.springboot.starter.webflux.tracer.ServerHttpRequestBuilderInject;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 注入Tracer流向下游的请求头
 *
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class SetTracerHeadersGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
        // 注入tracer信息到headers中
        Tracer tracer = GlobalTracer.get();
        Span activeSpan = tracer.activeSpan();
        if (activeSpan != null) {
            tracer.inject(activeSpan.context(), ServerHttpRequestBuilderInject.FORMAT, requestBuilder);
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        // 确保当前Filter是最后执行的，GatewayFilter和GlobalFilter排序是一起排的
        // https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/global-filters.html
        return Ordered.LOWEST_PRECEDENCE;
    }

}
