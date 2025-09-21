package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class ReactiveHttpSendMonoSpec<T> extends AbstractReactiveHttpSendSpec<Mono<T>> {

    private final Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Mono<T>> mapper;

    ReactiveHttpSendMonoSpec(HttpClient httpClient, HttpRequest httpRequest,
                             Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Mono<T>> mapper) {
        super(httpClient, httpRequest);
        Objects.requireNonNull(mapper, "mapper must not be null");
        this.mapper = mapper;
    }

    public Mono<T> send() {
        return send0().flatMap(mapper);
    }

    public <R> ReactiveHttpSendMonoSpec<R> flatMap(Function<T, R> mapper) {
        Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Mono<T>> map1 = this.mapper;
        Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Mono<R>> newMapper = res -> {
            Mono<T> mono1 = map1.apply(res);
            return mono1.map(mapper);
        };
        return new ReactiveHttpSendMonoSpec<>(httpClient, httpRequest, newMapper);
    }

}
