package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * 重定向默认5次，设置方式 -Djdk.httpclient.redirects.retrylimit=10
 *
 * @author fengwk
 */
@Slf4j
public class HttpClientUtils {

    /**
     * @see jdk.internal.net.http.common.Utils#getDisallowedHeaders()
     */
    private static final Set<String> HTTP_CLIENT_DISALLOW_HEADERS = Set.of(
        "connection", "content-length", "expect", "host", "upgrade");

    private HttpClientUtils() {}

    /**
     * 获取httpClient禁用的请求头
     *
     * @return httpClient禁用的请求头
     */
    public static Set<String> getHttpClientDisallowHeaders() {
        return HTTP_CLIENT_DISALLOW_HEADERS;
    }

    /**
     * 检查制定请求头是否被httpClient禁用
     *
     * @param headerName 请求头
     * @return 是否禁用
     */
    public static boolean isDisallowHeader(String headerName) {
        return HTTP_CLIENT_DISALLOW_HEADERS.contains(headerName.toLowerCase());
    }

    /**
     * 异步发送http请求，并使用SSE方式监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithSSEListener(
        HttpRequest httpRequest, SSEListener sseListener) {
        return sendAsyncWithSSEListener(HttpClientFactory.getDefaultHttpClient(), httpRequest, sseListener);
    }

    /**
     * 异步发送http请求，并使用SSE方式监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithSSEListener(
        HttpRequest httpRequest, SSEListener sseListener, boolean collectBody) {
        return sendAsyncWithSSEListener(HttpClientFactory.getDefaultHttpClient(), httpRequest, sseListener, collectBody);
    }

    /**
     * 异步发送http请求，并逐行监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithLineListener(
        HttpRequest httpRequest, StreamBodyListener<String> lineListener) {
        return sendAsyncWithLineListener(HttpClientFactory.getDefaultHttpClient(), httpRequest,
            lineListener);
    }

    /**
     * 异步发送http请求，并逐行监听
     *
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithLineListener(
        HttpRequest httpRequest, StreamBodyListener<String> lineListener, boolean collectBody) {
        return sendAsyncWithLineListener(HttpClientFactory.getDefaultHttpClient(), httpRequest,
            lineListener, collectBody);
    }

    /**
     * 异步发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsync(
        HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener) {
        return sendAsync(HttpClientFactory.getDefaultHttpClient(), httpRequest, listener);
    }

    /**
     * 异步发送http请求
     *
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsync(
        HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener, boolean collectBody) {
        return sendAsync(HttpClientFactory.getDefaultHttpClient(), httpRequest, listener, collectBody);
    }

    /* ------------------------------------------------------------------------------------------- */

    /**
     * 异步发送http请求，并使用SSE方式监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithSSEListener(
        HttpClient httpClient, HttpRequest httpRequest, SSEListener sseListener) {
        return sendAsyncWithSSEListener(httpClient, httpRequest, sseListener, false);
    }

    /**
     * 异步发送http请求，并使用SSE方式监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param sseListener 行字符串监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithSSEListener(
        HttpClient httpClient, HttpRequest httpRequest, SSEListener sseListener, boolean collectBody) {
        return sendAsyncWithLineListener(httpClient, httpRequest, new SSEListenerAdapter(sseListener), collectBody);
    }

    /**
     * 异步发送http请求，并逐行监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithLineListener(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<String> lineListener) {
        return sendAsyncWithLineListener(httpClient, httpRequest, lineListener, false);
    }

    /**
     * 异步发送http请求，并逐行监听
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param lineListener 行字符串监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsyncWithLineListener(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<String> lineListener, boolean collectBody) {
        return sendAsync(httpClient, httpRequest, new LineStreamBodyListenerAdapter(lineListener), collectBody);
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener) {
        return sendAsync(httpClient, httpRequest, listener, false);
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param listener 监听器
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    public static CompletableFuture<AsyncHttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest, StreamBodyListener<List<ByteBuffer>> listener,
        boolean collectBody) {
        return sendAsync(httpClient, httpRequest,
            responseInfo -> new StreamBodyListenerAdapter(responseInfo, listener), collectBody);
    }

    /**
     * 异步发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @param subscriberFactory 订阅者工厂
     * @param collectBody 是否收集请求体，开启后无法流式释放内存中的请求体数据
     * @return {@link HttpSendResult}
     */
    private static CompletableFuture<AsyncHttpSendResult> sendAsync(
        HttpClient httpClient, HttpRequest httpRequest,
        Function<HttpResponse.ResponseInfo, Flow.Subscriber<? super List<ByteBuffer>>> subscriberFactory,
        boolean collectBody) {
        BodyCollector bodyCollector = collectBody ? new BodyCollector() : null;
        return httpClient.sendAsync(
            httpRequest, fromSubscriber(subscriberFactory, bodyCollector))
            .thenApply(httpResponse -> {
                AsyncHttpSendResult result = new AsyncHttpSendResult(bodyCollector);
                result.setStatusCode(httpResponse.statusCode());
                HttpHeaders headers = httpResponse.headers();
                if (headers != null) {
                    result.setHeaders(Collections.unmodifiableMap(headers.map()));
                } else {
                    result.setHeaders(Collections.emptyMap());
                }
                return result;
            })
            .exceptionally(error -> {
                AsyncHttpSendResult result = new AsyncHttpSendResult(bodyCollector);
                result.setError(error);
                return result;
            });
    }

    /**
     * 支持gzip版本的{@link HttpResponse.BodyHandlers#fromSubscriber(Flow.Subscriber)}
     *
     * @param subscriberFactory subscriber factory
     * @return HttpResponse.BodyHandler<Void>
     */
    private static HttpResponse.BodyHandler<Void> fromSubscriber(
        Function<HttpResponse.ResponseInfo, Flow.Subscriber<? super List<ByteBuffer>>> subscriberFactory,
        BodyCollector bodyCollector) {
        return responseInfo -> {
            if (bodyCollector != null) {
                for (String contentType : responseInfo.headers().allValues(CONTENT_TYPE)) {
                    Charset charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
                    if (charset != null) {
                        bodyCollector.setCharset(charset);
                        break;
                    }
                }
            }
            return new GzipSupportBodySubscriber(
                responseInfo, subscriberFactory.apply(responseInfo), bodyCollector);
        };
    }

    /* ------------------------------------------------------------------------------------------- */

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
     * 发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpClient httpClient, HttpRequest httpRequest) {
        return doSend(httpClient, httpRequest);
    }

    private static HttpSendResult doSend(HttpClient httpClient, HttpRequest httpRequest) {
        HttpSendResult result = new HttpSendResult();
        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            result.setStatusCode(httpResponse.statusCode());
            result.setUri(httpResponse.uri());
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

}
