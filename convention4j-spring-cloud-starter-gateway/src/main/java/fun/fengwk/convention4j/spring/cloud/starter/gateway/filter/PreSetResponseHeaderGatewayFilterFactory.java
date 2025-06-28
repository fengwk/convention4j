package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

/**
 * 运行在前向filter时设置响应头
 *
 * @author fengwk
 */
public class PreSetResponseHeaderGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return new TracerContextGatewayFilter() {
            @Override
            public Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain, WebFluxTracerContext tc) {
                String value = ServerWebExchangeUtils.expand(exchange, config.getValue());
                exchange.getResponse().getHeaders().set(config.getName(), value);
                return chain.filter(exchange);
            }

            @Override
            public String toString() {
                return filterToStringCreator(PreSetResponseHeaderGatewayFilterFactory.this)
                    .append(config.getName(), config.getValue()).toString();
            }
        };
    }

}
