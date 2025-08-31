package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
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

    public static <T> Mono<ServerResponse> write(ServerRequest request, Result<T> result) {
        ServerResponse.BodyBuilder respBuilder = newBuildByStatus(request, result);
        respBuilder.contentType(MediaType.APPLICATION_JSON);
        Mono<Result<T>> resMono = Mono.just(result);
        return respBuilder.body(resMono, new ParameterizedTypeReference<>() {});
    }

    public static <T> Mono<ServerResponse> write(ServerRequest request, Result<T> result,
                                                 Consumer<ServerResponse.BodyBuilder> respConsumer) {
        ServerResponse.BodyBuilder respBuilder = newBuildByStatus(request, result);
        respConsumer.accept(respBuilder);
        respBuilder.contentType(MediaType.APPLICATION_JSON);
        Mono<Result<T>> resMono = Mono.just(result);
        return respBuilder.body(resMono, new ParameterizedTypeReference<>() {});
    }

    private static <T> ServerResponse.BodyBuilder newBuildByStatus(ServerRequest request, Result<T> result) {
        if (ResultInternalInvokerUtils.isIgnoreErrorHttpStatus(request.headers()::firstHeader)) {
            return status(HttpStatus.OK);
        } else {
            return status(result.getStatus());
        }
    }

}
