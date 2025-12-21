package fun.fengwk.convention4j.comfyui.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.common.http.client.ReactiveWebSocketClientUtils;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComfyUI WebSocket 客户端
 *
 * @author fengwk
 */
@Slf4j
public class ComfyUIWebSocket {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final String clientId;
    private final String apiKey;
    private final Duration websocketTimeout;
    private final Sinks.Many<ExecutionEvent> eventSink;
    private volatile Disposable webSocketSubscription;

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
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.websocketTimeout = websocketTimeout;
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();
        
        connect();
    }

    private void connect() {
        String wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://")
                + ComfyUIConstants.ApiPaths.WS + "?" + ComfyUIConstants.UrlParams.CLIENT_ID + "=" + clientId;
        URI uri = URI.create(wsUrl);

        log.info("Connecting to ComfyUI WebSocket: {}", wsUrl);

        // 准备自定义请求头
        Map<String, String> headers = null;
        if (apiKey != null && !apiKey.isEmpty()) {
            headers = new HashMap<>();
            headers.put(ComfyUIConstants.HttpHeaders.AUTHORIZATION,
                       ComfyUIConstants.HttpHeaders.BEARER_PREFIX + apiKey);
        }

        webSocketSubscription = ReactiveWebSocketClientUtils.connectText(httpClient, uri, headers, websocketTimeout)
                .doOnNext(this::handleMessage)
                .doOnError(e -> {
                    log.error("WebSocket error", e);
                    Exception exception = (e instanceof Exception) ? (Exception) e : new RuntimeException(e);
                    eventSink.tryEmitNext(new ExecutionEvent.Error("WebSocket error: " + e.getMessage(), exception));
                })
                .doOnComplete(() -> {
                    log.info("WebSocket connection closed");
                    // 发出连接关闭事件，确保下游不会收到空流
                    eventSink.tryEmitNext(new ExecutionEvent.ConnectionClosed(
                        "WebSocket connection closed unexpectedly without completion event"));
                })
                .subscribe();
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
                case EXECUTION_SUCCESS -> handleExecutionSuccess(content);
                case STATUS, PROGRESS_STATE, CRYSTOOLS_MONITOR -> {
                    // Ignore these types
                }
            }
        } catch (Exception e) {
            log.error("Failed to handle WebSocket message: {}", message, e);
        }
    }

    private void handleExecutionStart(JsonNode data) {
        // 根据ComfyUI API，execution_start 消息格式为：
        // {"type": "execution_start", "data": {"prompt_id": "...", "number": ..., "current": ...}}
        String promptId = data.path(ComfyUIConstants.JsonFields.PROMPT_ID).asText(null);
        if (promptId != null) {
            eventSink.tryEmitNext(new ExecutionEvent.Started(promptId));
        }
    }

    private void handleExecuting(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        if (nodeId == null) { // executing with null node means execution finished for this prompt (sometimes)
             return;
        }
        // prompt_id also available
        eventSink.tryEmitNext(new ExecutionEvent.NodeStarted(nodeId, "Unknown")); // Type is not provided in executing event
    }

    private void handleProgress(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        int value = data.path(ComfyUIConstants.JsonFields.VALUE).asInt(-1);
        int max = data.path(ComfyUIConstants.JsonFields.MAX).asInt(-1);
        if (nodeId != null && value >= 0 && max >= 0) {
            eventSink.tryEmitNext(new ExecutionEvent.NodeProgress(nodeId, value, max));
        }
    }

    private void handleExecuted(JsonNode data) {
        String nodeId = data.path(ComfyUIConstants.JsonFields.NODE).asText(null);
        // output also available
        if (nodeId != null) {
            eventSink.tryEmitNext(new ExecutionEvent.NodeCompleted(nodeId));
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
                eventSink.tryEmitNext(new ExecutionEvent.NodesCached(nodes));
            }
        }
    }
    
    private void handleExecutionError(JsonNode data) {
        String exceptionMessage = data.path(ComfyUIConstants.JsonFields.EXCEPTION_MESSAGE).asText(null);
        // exception_type, traceback also available
        eventSink.tryEmitNext(new ExecutionEvent.Error(exceptionMessage, new RuntimeException(exceptionMessage)));
    }

    private void handleExecutionSuccess(JsonNode data) {
        String promptId = data.path(ComfyUIConstants.JsonFields.PROMPT_ID).asText(null);
        if (promptId != null) {
            eventSink.tryEmitNext(new ExecutionEvent.ExecutionSucceeded(promptId));
        }
    }

    public Flux<ExecutionEvent> getEvents() {
        return eventSink.asFlux();
    }

    /**
     * 关闭WebSocket连接
     */
    public void close() {
        if (webSocketSubscription != null && !webSocketSubscription.isDisposed()) {
            log.debug("Closing WebSocket connection for client: {}", clientId);
            webSocketSubscription.dispose();
        }
        eventSink.tryEmitComplete();
    }
}