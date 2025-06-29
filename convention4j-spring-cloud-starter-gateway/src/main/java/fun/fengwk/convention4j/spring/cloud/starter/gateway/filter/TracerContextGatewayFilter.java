package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext.traceMono;

/**
 * {@link TracerContextGatewayFilter}实现了Tracer上下文功能，
 * 继承当前类并实现{@link #doFilter(ServerWebExchange, GatewayFilterChain)}
 * 将可以额外获取到{@link WebFluxTracerContext}
 *
 * @author fengwk
 */
public abstract class TracerContextGatewayFilter implements GatewayFilter {

    public abstract Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return traceMono(tc -> doFilter(exchange, chain));
    }

}
