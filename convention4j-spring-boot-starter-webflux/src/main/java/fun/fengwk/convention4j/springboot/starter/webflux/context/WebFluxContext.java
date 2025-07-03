package fun.fengwk.convention4j.springboot.starter.webflux.context;

import lombok.Getter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 作用域在一次请求中
 *
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

    public static WebFluxContext get(ContextView contextView) {
        if (contextView != null && contextView.hasKey(CONTEXT_KEY)) {
            return contextView.get(CONTEXT_KEY);
        }
        return null;
    }

}
