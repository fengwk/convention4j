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
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    // Each execution owns one socket; closing it completes this replay buffer and stops further growth.
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
        this.clientId = clientId;
        this.connectFuture = connect(httpClient, baseUrl, apiKey, websocketTimeout);
    }

    private CompletableFuture<WebSocketConnection> connect(
            HttpClient httpClient,
            String baseUrl,
            String apiKey,
            Duration websocketTimeout) {
        String wsBaseUrl = baseUrl.startsWith("https://")
                ? "wss://" + baseUrl.substring("https://".length())
                : "ws://" + baseUrl.substring("http://".length());
        String wsUrl = wsBaseUrl + ComfyUIConstants.ApiPaths.WS
                + "?" + ComfyUIConstants.UrlParams.CLIENT_ID + "=" + clientId;
        URI uri = URI.create(wsUrl);

        log.info("Connecting to ComfyUI WebSocket: {}", wsUrl);

        Map<String, String> headers = null;
        if (apiKey != null && !apiKey.isEmpty()) {
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
        if (error.getCause() != null) {
            return error.getCause();
        }
        return error;
    }

    private void handleMessage(String message) {
        log.debug("Received WebSocket message: {}", message);
        try {
            JsonNode root = JacksonUtils.readTree(message);
            if (root == null) {
                return;
            }

            String typeStr = root.path(ComfyUIConstants.JsonFields.TYPE).asText(null);
            if (typeStr == null) {
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
            log.error("Failed to handle WebSocket message: {}", message, e);
        }
    }

    private void handleExecutionStart(JsonNode data) {
        String promptId = promptId(data);
        if (promptId != null) {
            emit(promptId, new ExecutionEvent.Started(promptId));
        }
    }

    private void handleExecuting(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        if (nodeId != null) {
            emit(promptId(data), new ExecutionEvent.NodeStarted(nodeId, "Unknown"));
        }
    }

    private void handleProgress(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        int value = data.path(ComfyUIConstants.JsonFields.VALUE).asInt(-1);
        int max = data.path(ComfyUIConstants.JsonFields.MAX).asInt(-1);
        if (nodeId != null && value >= 0 && max >= 0) {
            emit(promptId(data), new ExecutionEvent.NodeProgress(nodeId, value, max));
        }
    }

    private void handleExecuted(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        if (nodeId != null) {
            emit(promptId(data), new ExecutionEvent.NodeCompleted(nodeId));
        }
    }

    private void handleExecutionCached(JsonNode data) {
        JsonNode nodesArray = data.path(ComfyUIConstants.JsonFields.NODES);
        if (nodesArray.isArray()) {
            List<String> nodes = new ArrayList<>();
            for (JsonNode node : nodesArray) {
                nodes.add(node.asText());
            }
            if (!nodes.isEmpty()) {
                emit(promptId(data), new ExecutionEvent.NodesCached(nodes));
            }
        }
    }

    private void handleExecutionError(JsonNode data) {
        String promptId = promptId(data);
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE_ID).asText(null);
        String exceptionMessage = data.path(ComfyUIConstants.JsonFields.EXCEPTION_MESSAGE).asText("Unknown error");
        String message = "Execution failed for prompt " + displayPromptId(promptId)
                + displayNodeId(nodeId) + ": " + exceptionMessage;
        emit(promptId, new ExecutionEvent.Error(message, new ExecutionException(message)));
    }

    private void handleExecutionInterrupted(JsonNode data) {
        String promptId = promptId(data);
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE_ID).asText(null);
        String message = "Execution interrupted for prompt " + displayPromptId(promptId) + displayNodeId(nodeId);
        emit(promptId, new ExecutionEvent.Error(message, new ExecutionException(message)));
    }

    private void handleExecutionSuccess(JsonNode data) {
        String promptId = promptId(data);
        if (promptId != null) {
            emit(promptId, new ExecutionEvent.ExecutionSucceeded(promptId));
        }
    }

    private String promptId(JsonNode data) {
        return data.path(ComfyUIConstants.JsonFields.PROMPT_ID).asText(null);
    }

    private String displayPromptId(String promptId) {
        return promptId == null ? "<unknown>" : promptId;
    }

    private String displayNodeId(String nodeId) {
        return nodeId == null ? "" : " at node " + nodeId;
    }

    private void handleConnectionError(Throwable error) {
        if (closed.get() || !connectionTerminated.compareAndSet(false, true)) {
            return;
        }
        log.error("WebSocket error for client {}", clientId, error);
        Exception exception = error instanceof Exception ? (Exception) error : new RuntimeException(error);
        readySink.tryEmitError(new ComfyUIException("Failed to connect ComfyUI WebSocket", exception));
        emit(null, new ExecutionEvent.Error("WebSocket error: " + error.getMessage(), exception));
        completeEvents();
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
        } else {
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
