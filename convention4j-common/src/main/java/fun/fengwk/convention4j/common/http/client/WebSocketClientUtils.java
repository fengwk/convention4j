package fun.fengwk.convention4j.common.http.client;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * WebSocket 客户端工具类
 *
 * @author fengwk
 */
@Slf4j
public class WebSocketClientUtils {

    private WebSocketClientUtils() {
    }

    /**
     * 使用默认 HttpClient 建立 WebSocket 连接
     *
     * @param uri      WebSocket URI
     * @param listener WebSocket 监听器
     * @return WebSocket 连接 Future
     */
    public static CompletableFuture<WebSocketConnection> connect(URI uri, WebSocketListener listener) {
        return connect(HttpClientFactory.getDefaultHttpClient(), uri, listener);
    }

    /**
     * 使用指定 HttpClient 建立 WebSocket 连接
     *
     * @param httpClient HttpClient 实例
     * @param uri        WebSocket URI
     * @param listener   WebSocket 监听器
     * @return WebSocket 连接 Future
     */
    public static CompletableFuture<WebSocketConnection> connect(HttpClient httpClient, URI uri, WebSocketListener listener) {
        return connect(httpClient, uri, null, null, listener);
    }

    /**
     * 使用指定 HttpClient 建立 WebSocket 连接，支持自定义请求头和连接超时
     *
     * @param httpClient     HttpClient 实例
     * @param uri            WebSocket URI
     * @param headers        自定义请求头（可选）
     * @param connectTimeout 连接超时时间（可选）
     * @param listener       WebSocket 监听器
     * @return WebSocket 连接 Future
     */
    public static CompletableFuture<WebSocketConnection> connect(
            HttpClient httpClient,
            URI uri,
            Map<String, String> headers,
            Duration connectTimeout,
            WebSocketListener listener) {
        CompletableFuture<WebSocketConnection> future = new CompletableFuture<>();

        WebSocket.Listener wsListener = new WebSocket.Listener() {
            private volatile WebSocketConnection connection;
            private final StringBuilder textBuffer = new StringBuilder();
            private final List<ByteBuffer> binaryBuffers = new ArrayList<>();

            @Override
            public void onOpen(WebSocket webSocket) {
                this.connection = new DefaultWebSocketConnection(webSocket);
                listener.onOpen(connection);
                future.complete(connection);
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                textBuffer.append(data);
                if (last) {
                    String completeMessage = textBuffer.toString();
                    textBuffer.setLength(0); // 清空缓冲区
                    listener.onMessage(completeMessage);
                }
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                // 复制 ByteBuffer 内容，因为原始 buffer 可能被复用
                ByteBuffer copy = ByteBuffer.allocate(data.remaining());
                copy.put(data);
                copy.flip();
                binaryBuffers.add(copy);

                if (last) {
                    // 合并所有二进制片段
                    int totalSize = binaryBuffers.stream().mapToInt(ByteBuffer::remaining).sum();
                    ByteBuffer combined = ByteBuffer.allocate(totalSize);
                    for (ByteBuffer buf : binaryBuffers) {
                        combined.put(buf);
                    }
                    combined.flip();
                    binaryBuffers.clear();
                    listener.onBinary(combined);
                }
                return WebSocket.Listener.super.onBinary(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                listener.onClose(statusCode, reason);
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                if (!future.isDone()) {
                    future.completeExceptionally(error);
                }
                listener.onError(error);
                WebSocket.Listener.super.onError(webSocket, error);
            }
        };

        WebSocket.Builder builder = httpClient.newWebSocketBuilder();
        
        // 添加自定义请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        
        // 设置连接超时
        if (connectTimeout != null) {
            builder.connectTimeout(connectTimeout);
        }
        
        builder.buildAsync(uri, wsListener)
                .exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }

    /**
     * WebSocketConnection 默认实现
     */
    private static class DefaultWebSocketConnection implements WebSocketConnection {
        private final WebSocket webSocket;

        public DefaultWebSocketConnection(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        @Override
        public CompletableFuture<Void> sendText(String message) {
            return webSocket.sendText(message, true).thenRun(() -> {});
        }

        @Override
        public CompletableFuture<Void> sendBinary(ByteBuffer data) {
            return webSocket.sendBinary(data, true).thenRun(() -> {});
        }

        @Override
        public CompletableFuture<Void> sendClose(int statusCode, String reason) {
            return webSocket.sendClose(statusCode, reason).thenRun(() -> {});
        }

        @Override
        public boolean isOpen() {
            return !webSocket.isOutputClosed() && !webSocket.isInputClosed();
        }

        @Override
        public void close() {
            // 正常关闭，状态码 1000
            sendClose(WebSocket.NORMAL_CLOSURE, "Close by client");
        }
    }
}