package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class ReactiveHttpSendSpec extends AbstractReactiveHttpSendSpec<Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>>> {

    ReactiveHttpSendSpec(HttpClient httpClient, HttpRequest httpRequest) {
        super(httpClient, httpRequest);
    }

    @Override
    public Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>> send() {
        return send0();
    }

    /**
     * 将响应转为 Mono
     */
    public <T> ReactiveHttpSendMonoSpec<T> toMono(Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Mono<T>> mapper) {
        return new ReactiveHttpSendMonoSpec<>(httpClient, httpRequest, mapper);
    }

    /**
     * 将响应转为 Flux
     */
    public <T> ReactiveHttpSendFluxSpec<T> toFlux(Function<ReactiveHttpSendResult<Flux<ByteBuffer>>, Flux<T>> mapper) {
        return new ReactiveHttpSendFluxSpec<>(httpClient, httpRequest, mapper);
    }

    /**
     * 基本的 http 响应信息
     */
    public ReactiveHttpSendMonoSpec<BaseHttpResponse> baseResponse() {
        return toMono(res -> {
            ReactiveHttpClientUtils.releaseBody(res.getBody());
            return Mono.just(res);
        });
    }

    /**
     * bytes响应体
     */
    public ReactiveHttpSendMonoSpec<byte[]> bodyToBytes() {
        return toMono(ReactiveHttpClientUtils::bodyToBytes);
    }

    /**
     * base64响应体
     */
    public ReactiveHttpSendMonoSpec<String> bodyToBase64() {
        return toMono(ReactiveHttpClientUtils::bodyToBase64);
    }

    /**
     * 写入文件
     */
    public ReactiveHttpSendMonoSpec<Void> bodyToFile(Path path, OpenOption... options) {
        return toMono(res -> ReactiveHttpClientUtils.bodyToFile(res, path, options));
    }

    /**
     * 字符串化的响应体
     */
    public ReactiveHttpSendMonoSpec<String> bodyToString() {
        return toMono(ReactiveHttpClientUtils::bodyToString);
    }

    /**
     * 流式输出chunk
     */
    public ReactiveHttpSendFluxSpec<ByteBuffer> bodyToFlux() {
        return toFlux(ReactiveHttpClientUtils::bodyToFlux);
    }

    /**
     * 流式输出每一行
     */
    public ReactiveHttpSendFluxSpec<String> bodyToLines() {
        return toFlux(ReactiveHttpClientUtils::bodyToLines);
    }

    /**
     * 流式输出 SSE
     */
    public ReactiveHttpSendFluxSpec<SSEEvent> bodyToSSE() {
        return toFlux(ReactiveHttpClientUtils::bodyToSSE);
    }

}
