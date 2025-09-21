package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class ReactiveHttpSendFluxSpec<T> extends AbstractReactiveHttpSendSpec<Flux<T>> {

    protected final Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Flux<T>> mapper;

    ReactiveHttpSendFluxSpec(HttpClient httpClient, HttpRequest httpRequest,
                             Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Flux<T>> mapper) {
        super(httpClient, httpRequest);
        Objects.requireNonNull(mapper, "mapper must not be null");
        this.mapper = mapper;
    }

    public Flux<T> send() {
        return send0().flatMapMany(mapper);
    }

    public <R> ReactiveHttpSendFluxSpec<R> flatMap(Function<T, R> mapper) {
        Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Flux<T>> map1 = this.mapper;
        Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Flux<R>> newMapper = res -> {
            Flux<T> flux1 = map1.apply(res);
            return flux1.map(mapper);
        };
        return new ReactiveHttpSendFluxSpec<>(httpClient, httpRequest, newMapper);
    }

}
