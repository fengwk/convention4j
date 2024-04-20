package fun.fengwk.convention4j.springboot.starter.webflux.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author fengwk
 */
@Slf4j
@Component
public class TestFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("test filter");
        return Mono.defer(() -> chain.filter(exchange));
//        return chain.filter(exchange);
    }

}
