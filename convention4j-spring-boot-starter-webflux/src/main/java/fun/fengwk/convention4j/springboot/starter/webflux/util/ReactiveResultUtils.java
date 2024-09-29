package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.api.result.Result;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

/**
 * @author fengwk
 */
public class ReactiveResultUtils {

    private ReactiveResultUtils() {
    }

    public static <T> Mono<ServerResponse> adapt(Result<T> result) {
        ServerResponse.BodyBuilder respBuilder = status(result.getStatus());
        respBuilder.contentType(MediaType.APPLICATION_JSON);
        Mono<Result<T>> resMono = Mono.just(result);
        return respBuilder.body(resMono, new ParameterizedTypeReference<>() {});
    }

    public static <T> Mono<ServerResponse> adapt(Result<T> result,
                                                 Consumer<ServerResponse.BodyBuilder> respConsumer) {
        ServerResponse.BodyBuilder respBuilder = status(result.getStatus());
        respConsumer.accept(respBuilder);
        respBuilder.contentType(MediaType.APPLICATION_JSON);
        Mono<Result<T>> resMono = Mono.just(result);
        return respBuilder.body(resMono, new ParameterizedTypeReference<>() {});
    }

}
