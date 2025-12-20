package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
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
        return Flux.create(sink -> {
            CompletableFuture<WebSocketConnection> connectFuture = WebSocketClientUtils.connect(httpClient, uri, new WebSocketListener() {
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
        return Flux.create(sink -> {
            CompletableFuture<WebSocketConnection> connectFuture = WebSocketClientUtils.connect(httpClient, uri, new WebSocketListener() {
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