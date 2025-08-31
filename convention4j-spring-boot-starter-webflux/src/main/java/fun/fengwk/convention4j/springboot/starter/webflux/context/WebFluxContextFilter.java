package fun.fengwk.convention4j.springboot.starter.webflux.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.function.Function;

/**
 * webflux处理器的过滤器，用于注入上下文
 *
 * @author fengwk
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class WebFluxContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(writeWebFluxContext(exchange));
    }

    private Function<Context, Context> writeWebFluxContext(ServerWebExchange exchange) {
        return ctx -> {
            WebFluxContext webFluxContext = new WebFluxContext(exchange);
            return WebFluxContext.set(ctx, webFluxContext);
        };
    }

}
