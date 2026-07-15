package fun.fengwk.convention4j.comfyui.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.exception.ComfyUIException;
import fun.fengwk.convention4j.comfyui.exception.ExecutionException;
import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.common.http.client.WebSocketClientUtils;
import fun.fengwk.convention4j.common.http.client.WebSocketConnection;
import fun.fengwk.convention4j.common.http.client.WebSocketListener;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ComfyUI WebSocket 客户端
 *
 * @author fengwk
 */
@Slf4j
public class ComfyUIWebSocket {

    private final String clientId;
    private final Sinks.One<Void> readySink = Sinks.one();
    // 每次执行独占连接；关闭时完成回放缓冲区，避免继续增长。
    private final Sinks.Many<EventEnvelope> eventSink = Sinks.many().replay().all();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean connectionTerminated = new AtomicBoolean();
    private volatile WebSocketConnection connection;
    private final CompletableFuture<WebSocketConnection> connectFuture;

    /**
     * 创建 ComfyUI WebSocket 客户端
     *
     * @param httpClient       HttpClient 实例
     * @param baseUrl          ComfyUI 服务器基础 URL
     * @param clientId         客户端 ID
     * @param apiKey           API 密钥（可选）
     * @param websocketTimeout WebSocket 连接超时时间（可选）
     */
    public ComfyUIWebSocket(HttpClient httpClient, String baseUrl, String clientId, String apiKey, Duration websocketTimeout) {
        if (httpClient == null) {
            throw new IllegalArgumentException("httpClient must not be null");
        }
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId must not be blank");
        }
        if (websocketTimeout != null && (websocketTimeout.isZero() || websocketTimeout.isNegative())) {
            throw new IllegalArgumentException("websocketTimeout must be positive when specified");
        }
        if (apiKey != null && containsControlCharacter(apiKey)) {
            throw new IllegalArgumentException("apiKey contains invalid header characters");
        }
        this.clientId = clientId;
        this.connectFuture = connect(httpClient, normalizeBaseUrl(baseUrl), apiKey, websocketTimeout);
    }

    private static boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeBaseUrl(String configuredBaseUrl) {
        if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        String normalized = configuredBaseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        URI uri;
        try {
            uri = URI.create(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("baseUrl must be a valid HTTP URL", e);
        }
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null
                || uri.getPort() == 0
                || uri.getPort() > 65535
                || uri.getRawUserInfo() != null
                || uri.getRawQuery() != null
                || uri.getRawFragment() != null) {
            throw new IllegalArgumentException(
                    "baseUrl must be an absolute HTTP URL without user-info, query or fragment");
        }
        String normalizedScheme = uri.getScheme().toLowerCase(Locale.ROOT);
        return normalizedScheme + normalized.substring(uri.getScheme().length());
    }

    private CompletableFuture<WebSocketConnection> connect(
            HttpClient httpClient,
            String baseUrl,
            String apiKey,
            Duration websocketTimeout) {
        String wsBaseUrl = baseUrl.startsWith("https://")
                ? "wss://" + baseUrl.substring("https://".length())
                : "ws://" + baseUrl.substring("http://".length());
        String encodedClientId = URLEncoder.encode(clientId, StandardCharsets.UTF_8).replace("+", "%20");
        String wsUrl = wsBaseUrl + ComfyUIConstants.ApiPaths.WS
                + "?" + ComfyUIConstants.UrlParams.CLIENT_ID + "=" + encodedClientId;
        URI uri = URI.create(wsUrl);

        log.info("Connecting to ComfyUI WebSocket: {}", wsUrl);

        Map<String, String> headers = null;
        if (apiKey != null && !apiKey.isBlank()) {
            headers = new HashMap<>();
            headers.put(ComfyUIConstants.HttpHeaders.AUTHORIZATION,
                    ComfyUIConstants.HttpHeaders.BEARER_PREFIX + apiKey);
        }

        CompletableFuture<WebSocketConnection> future = WebSocketClientUtils.connect(
                httpClient,
                uri,
                headers,
                websocketTimeout,
                new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocketConnection openedConnection) {
                        connection = openedConnection;
                        if (closed.get()) {
                            openedConnection.close();
                            return;
                        }
                        readySink.tryEmitEmpty();
                    }

                    @Override
                    public void onMessage(String message) {
                        handleMessage(message);
                    }

                    @Override
                    public void onClose(int statusCode, String reason) {
                        handleUnexpectedClose(statusCode, reason);
                    }

                    @Override
                    public void onError(Throwable error) {
                        handleConnectionError(error);
                    }
                });
        future.whenComplete((ignored, error) -> {
            if (error != null) {
                handleConnectionError(unwrapCompletionError(error));
            }
        });
        return future;
    }

    private Throwable unwrapCompletionError(Throwable error) {
        Throwable current = error;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private void handleMessage(String message) {
        log.debug("Received WebSocket message: {}", message);
        try {
            JsonNode root = JacksonUtils.readTree(message);
            if (root == null || !root.isObject()) {
                handleProtocolError("ComfyUI WebSocket message must be a JSON object", null);
                return;
            }

            String typeStr = textField(root, ComfyUIConstants.JsonFields.TYPE);
            if (typeStr == null) {
                handleProtocolError("ComfyUI WebSocket message is missing type", null);
                return;
            }

            WebSocketMessage type = WebSocketMessage.fromTypeString(typeStr);
            if (type == null) {
                log.warn("Unknown message type: {}", typeStr);
                return;
            }

            JsonNode content = root.path(ComfyUIConstants.JsonFields.DATA);
            switch (type) {
                case EXECUTION_START -> handleExecutionStart(content);
                case EXECUTING -> handleExecuting(content);
                case PROGRESS -> handleProgress(content);
                case EXECUTED -> handleExecuted(content);
                case EXECUTION_CACHED -> handleExecutionCached(content);
                case EXECUTION_ERROR -> handleExecutionError(content);
                case EXECUTION_INTERRUPTED -> handleExecutionInterrupted(content);
                case EXECUTION_SUCCESS -> handleExecutionSuccess(content);
                case STATUS, PROGRESS_STATE, CRYSTOOLS_MONITOR -> {
                    // Ignore these types.
                }
            }
        } catch (Exception e) {
            handleProtocolError("Failed to decode ComfyUI WebSocket message", e);
        }
    }

    private void handleExecutionStart(JsonNode data) {
        String promptId = requiredPromptId(data, "execution_start");
        if (promptId == null) {
            return;
        }
        emit(promptId, new ExecutionEvent.Started(promptId));
    }

    private void handleExecuting(JsonNode data) {
        String promptId = requiredPromptId(data, "executing");
        if (promptId == null) {
            return;
        }
        String nodeId = textField(data, ComfyUIConstants.JsonFields.NODE);
        if (nodeId != null) {
            emit(promptId, new ExecutionEvent.NodeStarted(nodeId, "Unknown"));
        }
    }

    private void handleProgress(JsonNode data) {
        String promptId = requiredPromptId(data, "progress");
        if (promptId == null) {
            return;
        }
        String nodeId = textField(data, ComfyUIConstants.JsonFields.NODE);
        Integer value = nonNegativeIntegerField(data, ComfyUIConstants.JsonFields.VALUE);
        Integer max = nonNegativeIntegerField(data, ComfyUIConstants.JsonFields.MAX);
        if (nodeId != null && value != null && max != null) {
            emit(promptId, new ExecutionEvent.NodeProgress(nodeId, value, max));
        }
    }

    private void handleExecuted(JsonNode data) {
        String promptId = requiredPromptId(data, "executed");
        if (promptId == null) {
            return;
        }
        String nodeId = textField(data, ComfyUIConstants.JsonFields.NODE);
        if (nodeId != null) {
            emit(promptId, new ExecutionEvent.NodeCompleted(nodeId));
        }
    }

    private void handleExecutionCached(JsonNode data) {
        String promptId = requiredPromptId(data, "execution_cached");
        if (promptId == null) {
            return;
        }
        JsonNode nodesArray = data.path(ComfyUIConstants.JsonFields.NODES);
        if (nodesArray.isArray()) {
            List<String> nodes = new ArrayList<>();
            for (JsonNode node : nodesArray) {
                if (node.isTextual() && !node.asText().isBlank()) {
                    nodes.add(node.asText());
                }
            }
            if (!nodes.isEmpty()) {
                emit(promptId, new ExecutionEvent.NodesCached(nodes));
            }
        }
    }

    private void handleExecutionError(JsonNode data) {
        String promptId = requiredPromptId(data, "execution_error");
        if (promptId == null) {
            return;
        }
        String nodeId = textField(data, ComfyUIConstants.JsonFields.NODE_ID);
        String exceptionMessage = textField(data, ComfyUIConstants.JsonFields.EXCEPTION_MESSAGE);
        if (exceptionMessage == null) {
            exceptionMessage = "Unknown error";
        }
        String message = "Execution failed for prompt " + displayPromptId(promptId)
                + displayNodeId(nodeId) + ": " + exceptionMessage;
        emit(promptId, new ExecutionEvent.Error(message, new ExecutionException(message)));
    }

    private void handleExecutionInterrupted(JsonNode data) {
        String promptId = requiredPromptId(data, "execution_interrupted");
        if (promptId == null) {
            return;
        }
        String nodeId = textField(data, ComfyUIConstants.JsonFields.NODE_ID);
        String message = "Execution interrupted for prompt " + displayPromptId(promptId) + displayNodeId(nodeId);
        emit(promptId, new ExecutionEvent.Error(message, new ExecutionException(message)));
    }

    private void handleExecutionSuccess(JsonNode data) {
        String promptId = requiredPromptId(data, "execution_success");
        if (promptId == null) {
            return;
        }
        emit(promptId, new ExecutionEvent.ExecutionSucceeded(promptId));
    }

    private String requiredPromptId(JsonNode data, String messageType) {
        String promptId = textField(data, ComfyUIConstants.JsonFields.PROMPT_ID);
        if (promptId == null) {
            handleProtocolError("ComfyUI " + messageType + " message is missing prompt_id", null);
        }
        return promptId;
    }

    private String textField(JsonNode data, String fieldName) {
        if (data == null || !data.isObject()) {
            return null;
        }
        JsonNode field = data.get(fieldName);
        if (field == null || !field.isTextual() || field.asText().isBlank()) {
            return null;
        }
        return field.asText();
    }

    private Integer nonNegativeIntegerField(JsonNode data, String fieldName) {
        if (data == null || !data.isObject()) {
            return null;
        }
        JsonNode field = data.get(fieldName);
        if (field == null || !field.isIntegralNumber() || !field.canConvertToInt()) {
            return null;
        }
        int value = field.asInt();
        return value < 0 ? null : value;
    }

    private String displayPromptId(String promptId) {
        return promptId == null ? "<unknown>" : promptId;
    }

    private String displayNodeId(String nodeId) {
        return nodeId == null ? "" : " at node " + nodeId;
    }

    private void handleProtocolError(String message, Throwable cause) {
        if (closed.get() || !connectionTerminated.compareAndSet(false, true)) {
            return;
        }
        ComfyUIException exception = cause == null
                ? new ComfyUIException(message)
                : new ComfyUIException(message, cause);
        log.error("{} for client {}", message, clientId, cause);
        readySink.tryEmitError(exception);
        emit(null, new ExecutionEvent.Error(message, exception));
        completeEvents();
        WebSocketConnection current = connection;
        if (current != null && current.isOpen()) {
            current.close();
        }
    }

    private void handleConnectionError(Throwable error) {
        if (closed.get() || !connectionTerminated.compareAndSet(false, true)) {
            return;
        }
        log.error("WebSocket error for client {}", clientId, error);
        ComfyUIException exception = new ComfyUIException("ComfyUI WebSocket transport failed", error);
        readySink.tryEmitError(exception);
        emit(null, new ExecutionEvent.Error("WebSocket error: " + error.getMessage(), exception));
        completeEvents();
        WebSocketConnection current = connection;
        if (current != null && current.isOpen()) {
            current.close();
        }
    }

    private void handleUnexpectedClose(int statusCode, String reason) {
        if (closed.get() || !connectionTerminated.compareAndSet(false, true)) {
            return;
        }
        String message = "WebSocket connection closed unexpectedly (status=" + statusCode + ", reason=" + reason + ")";
        log.warn("{} for client {}", message, clientId);
        readySink.tryEmitError(new ComfyUIException(message));
        emit(null, new ExecutionEvent.ConnectionClosed(message));
        completeEvents();
    }

    private synchronized void emit(String promptId, ExecutionEvent event) {
        if (!closed.get()) {
            eventSink.tryEmitNext(new EventEnvelope(promptId, event));
        }
    }

    private synchronized void completeEvents() {
        eventSink.tryEmitComplete();
    }

    /**
     * 等待WebSocket连接建立。
     */
    public Mono<Void> awaitReady() {
        return readySink.asMono();
    }

    /**
     * 获取指定prompt的执行事件。连接级事件始终会传递。
     */
    public Flux<ExecutionEvent> getEvents(String promptId) {
        if (promptId == null || promptId.isBlank()) {
            return Flux.error(new IllegalArgumentException("promptId must not be blank"));
        }
        return eventSink.asFlux()
                .filter(envelope -> envelope.promptId() == null || envelope.promptId().equals(promptId))
                .map(EventEnvelope::event);
    }

    /**
     * 获取所有执行事件。
     */
    public Flux<ExecutionEvent> getEvents() {
        return eventSink.asFlux().map(EventEnvelope::event);
    }

    public boolean isClosed() {
        return closed.get();
    }

    /**
     * 连接是否已因远端关闭、传输错误或协议错误而不可继续使用。
     */
    public boolean isConnectionTerminated() {
        return connectionTerminated.get();
    }

    /**
     * 关闭WebSocket连接。
     */
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        log.debug("Closing WebSocket connection for client: {}", clientId);
        readySink.tryEmitError(new ComfyUIException("ComfyUI WebSocket closed before connection became ready"));
        WebSocketConnection current = connection;
        if (current != null && current.isOpen()) {
            current.close();
        } else if (!connectFuture.cancel(true)) {
            connectFuture.thenAccept(openedConnection -> {
                if (openedConnection.isOpen()) {
                    openedConnection.close();
                }
            });
        }
        completeEvents();
    }

    private record EventEnvelope(String promptId, ExecutionEvent event) {
    }
}
