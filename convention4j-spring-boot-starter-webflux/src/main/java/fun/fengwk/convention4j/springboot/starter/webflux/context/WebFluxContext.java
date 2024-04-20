package fun.fengwk.convention4j.springboot.starter.webflux.context;

import lombok.Getter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
public class WebFluxContext {

    private static final String CONTEXT_KEY = WebFluxContext.class.getName();

    @Getter
    private final ServerHttpRequest request;
    @Getter
    private final ServerHttpResponse response;
    @Getter
    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    public WebFluxContext(ServerHttpRequest request, ServerHttpResponse response) {
        this.request = Objects.requireNonNull(request, "Request must not be null");
        this.response = Objects.requireNonNull(response, "Response must not be null");
    }

    public static Context set(Context context, WebFluxContext webFluxContext) {
        return context.put(CONTEXT_KEY, webFluxContext);
    }

    public static Mono<Optional<WebFluxContext>> get() {
        return Mono.deferContextual(ctx -> {
            WebFluxContext webFluxContext = (WebFluxContext) ctx.getOrEmpty(CONTEXT_KEY).orElse(null);
            return Mono.just(Optional.ofNullable(webFluxContext));
        });
    }

}
