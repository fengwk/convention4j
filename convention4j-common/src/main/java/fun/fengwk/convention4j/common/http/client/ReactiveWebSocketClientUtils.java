package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 响应式 WebSocket 客户端工具类
 *
 * @author fengwk
 */
public class ReactiveWebSocketClientUtils {

    private ReactiveWebSocketClientUtils() {
    }

    /**
     * 响应式 WebSocket 连接，返回文本消息 Flux
     *
     * @param uri WebSocket URI
     * @return 文本消息 Flux
     */
    public static Flux<String> connectText(URI uri) {
        return connectText(HttpClientFactory.getDefaultHttpClient(), uri);
    }

    /**
     * 响应式 WebSocket 连接，使用指定 HttpClient，返回文本消息 Flux
     *
     * @param httpClient HttpClient 实例
     * @param uri        WebSocket URI
     * @return 文本消息 Flux
     */
    public static Flux<String> connectText(HttpClient httpClient, URI uri) {
        return connectText(httpClient, uri, null, null);
    }

    /**
     * 响应式 WebSocket 连接，使用指定 HttpClient，支持自定义请求头和连接超时，返回文本消息 Flux
     *
     * @param httpClient     HttpClient 实例
     * @param uri            WebSocket URI
     * @param headers        自定义请求头（可选）
     * @param connectTimeout 连接超时时间（可选）
     * @return 文本消息 Flux
     */
    public static Flux<String> connectText(
            HttpClient httpClient,
            URI uri,
            Map<String, String> headers,
            Duration connectTimeout) {
        return Flux.create(sink -> {
            CompletableFuture<WebSocketConnection> connectFuture = WebSocketClientUtils.connect(httpClient, uri, headers, connectTimeout, new WebSocketListener() {
                @Override
                public void onOpen(WebSocketConnection connection) {
                    // 连接建立
                }

                @Override
                public void onMessage(String message) {
                    sink.next(message);
                }

                @Override
                public void onClose(int statusCode, String reason) {
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
            
            sink.onDispose(() -> {
                connectFuture.thenAccept(connection -> {
                    if (connection.isOpen()) {
                        connection.close();
                    }
                });
            });
        });
    }

    /**
     * 响应式 WebSocket 连接，返回二进制消息 Flux
     *
     * @param uri WebSocket URI
     * @return 二进制消息 Flux
     */
    public static Flux<ByteBuffer> connectBinary(URI uri) {
        return connectBinary(HttpClientFactory.getDefaultHttpClient(), uri);
    }

    /**
     * 响应式 WebSocket 连接，使用指定 HttpClient，返回二进制消息 Flux
     *
     * @param httpClient HttpClient 实例
     * @param uri        WebSocket URI
     * @return 二进制消息 Flux
     */
    public static Flux<ByteBuffer> connectBinary(HttpClient httpClient, URI uri) {
        return connectBinary(httpClient, uri, null, null);
    }

    /**
     * 响应式 WebSocket 连接，使用指定 HttpClient，支持自定义请求头和连接超时，返回二进制消息 Flux
     *
     * @param httpClient     HttpClient 实例
     * @param uri            WebSocket URI
     * @param headers        自定义请求头（可选）
     * @param connectTimeout 连接超时时间（可选）
     * @return 二进制消息 Flux
     */
    public static Flux<ByteBuffer> connectBinary(
            HttpClient httpClient,
            URI uri,
            Map<String, String> headers,
            Duration connectTimeout) {
        return Flux.create(sink -> {
            CompletableFuture<WebSocketConnection> connectFuture = WebSocketClientUtils.connect(httpClient, uri, headers, connectTimeout, new WebSocketListener() {
                @Override
                public void onOpen(WebSocketConnection connection) {
                    // 连接建立
                }

                @Override
                public void onBinary(ByteBuffer data) {
                    sink.next(data);
                }

                @Override
                public void onClose(int statusCode, String reason) {
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });

            sink.onDispose(() -> {
                connectFuture.thenAccept(connection -> {
                    if (connection.isOpen()) {
                        connection.close();
                    }
                });
            });
        });
    }
}