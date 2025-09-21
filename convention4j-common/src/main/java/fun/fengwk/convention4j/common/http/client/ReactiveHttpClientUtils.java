package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * {@link HttpClientUtils} Reactive 版本实现
 *
 * @author fengwk
 */
public class ReactiveHttpClientUtils {

    private ReactiveHttpClientUtils() {
    }

    /**
     * 构建异步请求规约
     *
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static ReactiveHttpSendSpec buildSpec(HttpRequest httpRequest) {
        return new ReactiveHttpSendSpec(HttpClientFactory.getDefaultHttpClient(), httpRequest);
    }

    /**
     * 构建异步请求规约
     *
     * @param httpClient  {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static ReactiveHttpSendSpec buildSpec(HttpClient httpClient, HttpRequest httpRequest) {
        return new ReactiveHttpSendSpec(httpClient, httpRequest);
    }

    /**
     * 释放响应体
     */
    public static <T> void releaseBody(Flux<T> body) {
        if (body != null) {
            body.subscribe().dispose();
        }
    }

    /**
     * 释放响应体
     */
    public static <T> void releaseBody(Mono<T> body) {
        if (body != null) {
            body.subscribe().dispose();
        }
    }

    public static Mono<String> bodyToStringMono(ReactiveHttpSendResult<Flux<ByteBuffer>> res) {
        String contentType = res.getFirstHeader(CONTENT_TYPE);
        Charset charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
        return res.getBody()
            .collect(() -> Collections.synchronizedList(new ArrayList<ByteBuffer>()), List::add)
            .map(BufferUtils::toBytes)
            .map(bytes -> new String(bytes, charset));
    }

    public static Flux<String> bodyToLineFlux(ReactiveHttpSendResult<Flux<ByteBuffer>> res) {
        String contentType = res.getFirstHeader(CONTENT_TYPE);
        Charset charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
        LineStreamBodyDecoder lineDecoder = new LineStreamBodyDecoder(charset);
        return res.getBody()
            .flatMap(chunk -> {
                try {
                    lineDecoder.appendChunk(chunk);
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = lineDecoder.nextLine()) != null) {
                        lines.add(line);
                    }
                    return Flux.fromIterable(lines);
                } catch (CharacterCodingException ex) {
                    return Flux.error(new HttpResponseException(ex, res));
                }
            });
    }

    public static Flux<SSEEvent> bodyToSSEFlux(ReactiveHttpSendResult<Flux<ByteBuffer>> res) {
        String contentType = res.getFirstHeader(CONTENT_TYPE);
        Charset charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
        LineStreamBodyDecoder lineDecoder = new LineStreamBodyDecoder(charset);
        SSEDecoder sseDecoder = new SSEDecoder();
        return res.getBody()
            .flatMap(chunk -> {
                try {
                    lineDecoder.appendChunk(chunk);
                    String line;
                    while ((line = lineDecoder.nextLine()) != null) {
                        sseDecoder.appendLine(line);
                    }

                    List<SSEEvent> sseEvents = new ArrayList<>();
                    SSEEvent sseEvent;
                    while ((sseEvent = sseDecoder.nextEvent()) != null) {
                        sseEvents.add(sseEvent);
                    }
                    return Flux.fromIterable(sseEvents);
                } catch (CharacterCodingException ex) {
                    return Flux.error(new HttpResponseException(ex, res));
                }
            });
    }

}
