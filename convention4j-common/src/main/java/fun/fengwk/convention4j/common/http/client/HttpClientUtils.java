package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import static fun.fengwk.convention4j.common.http.HttpHeaders.LOCATION;

/**
 * @author fengwk
 */
@Slf4j
public class HttpClientUtils {

    private HttpClientUtils() {}

    /**
     * 支持gzip版本的{@link HttpResponse.BodyHandlers#fromSubscriber(Flow.Subscriber)}
     *
     * @param subscriber subscriber
     * @return HttpResponse.BodyHandler<List<ByteBuffer>>
     */
    public static HttpResponse.BodyHandler<List<ByteBuffer>> fromSubscriber(
        Flow.Subscriber<? super List<ByteBuffer>> subscriber) {
        return fromSubscriber(responseInfo -> subscriber);
    }

    /**
     * 支持gzip版本的{@link HttpResponse.BodyHandlers#fromSubscriber(Flow.Subscriber)}
     *
     * @param subscriberFactory subscriber factory
     * @return HttpResponse.BodyHandler<List<ByteBuffer>>
     */
    public static HttpResponse.BodyHandler<List<ByteBuffer>> fromSubscriber(
        Function<HttpResponse.ResponseInfo, Flow.Subscriber<? super List<ByteBuffer>>> subscriberFactory) {
        return responseInfo -> new GzipSupportBodySubscriber(responseInfo, subscriberFactory.apply(responseInfo));
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}异步发送http请求，并使用SSE方式监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsyncWithSSEListener(
        HttpRequest httpRequest, SSEListener sseListener) {
        return sendAsyncWithLineListener(HttpClientFactory.getDefaultHttpClient(), httpRequest, new SSEListenerAdapter(sseListener));
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}异步发送http请求，并逐行监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsyncWithLineListener(
        HttpRequest httpRequest, StreamBodyListener<String> lineListener) {
        return sendAsyncWithLineListener(HttpClientFactory.getDefaultHttpClient(), httpRequest, lineListener);
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}异步发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener) {
        return sendAsync(HttpClientFactory.getDefaultHttpClient(), httpRequest, listener);
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}异步发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @param subscriber 订阅者
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpRequest httpRequest, Flow.Subscriber<? super List<ByteBuffer>> subscriber) {
        return sendAsync(HttpClientFactory.getDefaultHttpClient(), httpRequest, subscriber);
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}异步发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @param subscriberFactory 订阅者工厂
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpRequest httpRequest,
        Function<HttpResponse.ResponseInfo, Flow.Subscriber<? super List<ByteBuffer>>> subscriberFactory) {
        return sendAsync(HttpClientFactory.getDefaultHttpClient(), httpRequest, subscriberFactory);
    }

    /**
     * 异步发送http请求，并使用SSE方式监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsyncWithSSEListener(
        HttpClient httpClient, HttpRequest httpRequest, SSEListener sseListener) {
        return sendAsyncWithLineListener(httpClient, httpRequest, new SSEListenerAdapter(sseListener));
    }

    /**
     * 异步发送http请求，并逐行监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsyncWithLineListener(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<String> lineListener) {
        return sendAsync(httpClient, httpRequest, new LineStreamBodyListenerAdapter(lineListener));
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener) {
        return sendAsync(httpClient, httpRequest,
            responseInfo -> new StreamBodyListenerAdapter(responseInfo, listener));
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param subscriber 订阅者
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest, Flow.Subscriber<? super List<ByteBuffer>> subscriber) {
        return sendAsync(httpClient, httpRequest, responseInfo -> subscriber);
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param subscriberFactory 订阅者工厂
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<HttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest,
        Function<HttpResponse.ResponseInfo, Flow.Subscriber<? super List<ByteBuffer>>> subscriberFactory) {
        return httpClient.sendAsync(
            httpRequest, fromSubscriber(subscriberFactory))
            .thenApply(httpResponse -> {
                HttpSendResult result = new HttpSendResult();
                result.setStatusCode(httpResponse.statusCode());
                HttpHeaders headers = httpResponse.headers();
                if (headers != null) {
                    result.setHeaders(Collections.unmodifiableMap(headers.map()));
                } else {
                    result.setHeaders(Collections.emptyMap());
                }
                List<ByteBuffer> byteBuffers = httpResponse.body();
                byte[] bodyBytes = toBytes(byteBuffers);
                result.setBodyBytes(bodyBytes);
                return result;
            })
            .exceptionally(error -> {
                HttpSendResult result = new HttpSendResult();
                result.setError(error);
                return result;
            });
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpRequest httpRequest) {
        return send(HttpClientFactory.getDefaultHttpClient(), httpRequest);
    }

    /**
     * 使用{@link HttpClientFactory#getDefaultHttpClient()}发送http请求
     *
     * @param httpRequestBuilder {@link HttpRequest.Builder}
     * @param redirectCount 支持重定向的次数
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpRequest.Builder httpRequestBuilder, int redirectCount) {
        return send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder, redirectCount);
    }

    /**
     * 发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpClient httpClient, HttpRequest httpRequest) {
        return doSend(httpClient, httpRequest);
    }

    /**
     * 发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequestBuilder {@link HttpRequest.Builder}
     * @param redirectCount 支持重定向的次数
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpClient httpClient, HttpRequest.Builder httpRequestBuilder, int redirectCount) {
        HttpRequest.Builder copied = httpRequestBuilder.copy();
        HttpRequest httpRequest = copied.build();
        // 首次请求
        HttpSendResult sendResult = doSend(httpClient, httpRequest);

        String location;
        while (redirectCount > 0 && needRedirect(sendResult.getStatusCode())
            && (location = sendResult.getFirstHeader(LOCATION)) != null) {
            // 解析Location，如果错误将终止
            try {
                URI uri = URI.create(location);
                httpRequest = copied.uri(uri).build();
            } catch (IllegalArgumentException ex) {
                IllegalArgumentException locationEx = new IllegalArgumentException(String.format("invalid location '%s'", location), ex);
                sendResult.setError(locationEx);
                return sendResult;
            }

            // 发送新的重定向请求
            HttpSendResult redirectSendResult = doSend(httpClient, httpRequest);

            // 关闭旧的结果
            sendResult.close();

            // 使用新的结果代替
            sendResult = redirectSendResult;

            // 减少重定向计数
            redirectCount--;
        }

        // 返回最终的结果
        return sendResult;
    }

    private static HttpSendResult doSend(HttpClient httpClient, HttpRequest httpRequest) {
        HttpSendResult result = new HttpSendResult();
        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            result.setStatusCode(httpResponse.statusCode());
            HttpHeaders headers = httpResponse.headers();
            if (headers != null) {
                result.setHeaders(Collections.unmodifiableMap(headers.map()));
            } else {
                result.setHeaders(Collections.emptyMap());
            }
            try {
                InputStream body = httpResponse.body();
                if (body != null && gzip(headers)) {
                    body = new GZIPInputStream(body);
                }
                result.setBody(body);
            } catch (Throwable err) {
                log.error("http client parse body error", err);
                result.setError(err);
            }
        } catch (IOException ex) {
            log.error("http client send failed", ex);
            result.setError(ex);
        } catch (InterruptedException ex) {
            log.warn("http client send interrupted", ex);
            result.setError(ex);
            Thread.currentThread().interrupt();
        } catch (Throwable err) {
            log.error("http client send error", err);
            result.setError(err);
        }
        return result;
    }

    private static boolean gzip(HttpHeaders headers) {
        if (headers == null) {
            return false;
        }
        for (String contentEncoding : headers.allValues("Content-Encoding")) {
            if (HttpUtils.gzip(contentEncoding)) {
                return true;
            }
        }
        return false;
    }

    private static boolean needRedirect(int statusCode) {
        return statusCode == HttpStatus.MOVED_PERMANENTLY.getStatus()
            || statusCode == HttpStatus.FOUND.getStatus()
            || statusCode == HttpStatus.SEE_OTHER.getStatus()
            || statusCode == HttpStatus.TEMPORARY_REDIRECT.getStatus()
            || statusCode == HttpStatus.PERMANENT_REDIRECT.getStatus();
    }

    private static byte[] toBytes(List<ByteBuffer> byteBuffers) {
        int len = 0;
        for (ByteBuffer byteBuffer : byteBuffers) {
            len += byteBuffer.remaining();
        }
        byte[] bodyBytes = new byte[len];
        int offset = 0;
        for (ByteBuffer byteBuffer : byteBuffers) {
            int rem = byteBuffer.remaining();
            byteBuffer.get(bodyBytes, offset, rem);
            offset += rem;
        }
        return bodyBytes;
    }

}
