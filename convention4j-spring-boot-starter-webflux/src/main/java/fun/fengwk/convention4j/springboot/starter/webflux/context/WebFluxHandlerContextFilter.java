package fun.fengwk.convention4j.springboot.starter.webflux.context;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
public class WebFluxHandlerContextFilter extends WebFluxContextSupport implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(contextModifier(exchange))
            .then(finisher(exchange))
            .doOnError(err -> {
                ServerHttpRequest request = exchange.getRequest();
                log.error("An error occurred during execution, method: {}, path: {}, remoteAddress: {}",
                    request.getMethod(), request.getPath(), request.getRemoteAddress(), err);
                Span activeSpan = GlobalTracer.get().activeSpan();
                if (activeSpan != null) {
                    activeSpan.setTag(Tags.ERROR, true);
                }
                finisher(exchange).subscribe();
            });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
