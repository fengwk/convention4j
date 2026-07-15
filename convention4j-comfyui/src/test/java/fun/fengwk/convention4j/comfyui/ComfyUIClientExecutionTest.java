package fun.fengwk.convention4j.comfyui;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fun.fengwk.convention4j.comfyui.exception.ComfyUIException;
import fun.fengwk.convention4j.comfyui.exception.ExecutionException;
import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionListener;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.input.InputFile;
import fun.fengwk.convention4j.comfyui.websocket.ComfyUIWebSocket;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * WebSocket执行链路回归测试
 *
 * @author fengwk
 */
public class ComfyUIClientExecutionTest {

    private HttpServer server;
    private ExecutorService serverExecutor;

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        if (serverExecutor != null) {
            serverExecutor.shutdownNow();
        }
    }

    @Test
    public void testConcurrentExecutionsWaitForReadyBufferEventsAndIgnoreOtherPromptInterruption() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        AtomicInteger promptRequests = new AtomicInteger();
        CountDownLatch bothPromptsReceived = new CountDownLatch(2);
        AtomicBoolean eventsSent = new AtomicBoolean();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                JsonNode body = JacksonUtils.readTree(readBody(exchange));
                String clientId = body.path("client_id").asText();
                ControlledWebSocketHttpClient httpClient = httpClientRef.get();
                int socketIndex = httpClient.indexOfClientId(clientId);
                String promptId = "prompt-" + (socketIndex + 1);
                promptRequests.incrementAndGet();
                bothPromptsReceived.countDown();
                if (!await(bothPromptsReceived)) {
                    respondJson(exchange, 500, "{\"error\":\"second prompt was not submitted\"}");
                    return;
                }
                if (eventsSent.compareAndSet(false, true)) {
                    ControlledWebSocket first = httpClient.getSocket(0);
                    ControlledWebSocket second = httpClient.getSocket(1);
                    first.receive(event("execution_start", "{\"prompt_id\":\"prompt-1\"}"));
                    second.receive(event("execution_start", "{\"prompt_id\":\"prompt-2\"}"));
                    String interruption = event(
                            "execution_interrupted",
                            "{\"prompt_id\":\"prompt-1\",\"node_id\":\"7\",\"node_type\":\"TestNode\"}");
                    first.receive(interruption);
                    second.receive(interruption);
                    second.receive(event("execution_success", "{\"prompt_id\":\"prompt-2\"}"));
                }
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"" + promptId + "\",\"number\":1,\"node_errors\":{}}");
            } else if (path.startsWith("/history/")) {
                String promptId = path.substring("/history/".length());
                respondJson(exchange, 200, "{\"" + promptId + "\":{\"outputs\":{}}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> firstResult = client.execute(workflow()).toFuture();
            CompletableFuture<ExecutionResult> secondResult = client.execute(workflow()).toFuture();

            httpClient.awaitSocketCount(2);
            // 两个订阅分别创建连接，连接ready前不得发送 /prompt。
            Assertions.assertEquals(0, promptRequests.get());
            Assertions.assertNotEquals(httpClient.getSocket(0).getClientId(), httpClient.getSocket(1).getClientId());
            httpClient.getSocket(0).open();
            httpClient.getSocket(1).open();

            java.util.concurrent.ExecutionException interrupted = Assertions.assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> firstResult.get(5, TimeUnit.SECONDS));
            Assertions.assertInstanceOf(ExecutionException.class, interrupted.getCause());
            Assertions.assertTrue(interrupted.getCause().getMessage().contains("prompt-1"));
            Assertions.assertTrue(interrupted.getCause().getMessage().contains("node 7"));

            ExecutionResult completed = secondResult.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals("prompt-2", completed.getPromptId());
            // interruption被广播到第二个socket，但按promptId过滤后没有污染第二次执行。
            httpClient.getSocket(0).awaitClose();
            httpClient.getSocket(1).awaitClose();
        }
    }

    @Test
    public void testExecutionRetriesOnlyUntilHistoryEntryAppears() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        AtomicInteger historyRequests = new AtomicInteger();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"history-race\"}"));
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"history-race\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/history-race".equals(path)) {
                int request = historyRequests.incrementAndGet();
                respondJson(exchange, 200, request < 3 ? "{}" : "{\"history-race\":{\"outputs\":{}}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow()).toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();

            // execution_success先于历史持久化时，仅缺失prompt条目会被短暂重试。
            ExecutionResult result = resultFuture.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals("history-race", result.getPromptId());
            Assertions.assertEquals(3, historyRequests.get());
        }
    }

    @Test
    public void testExecutionHistoryMalformedResponseIsNotRetried() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        AtomicInteger historyRequests = new AtomicInteger();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"malformed-history\"}"));
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"malformed-history\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/malformed-history".equals(path)) {
                historyRequests.incrementAndGet();
                respondJson(exchange, 200, "{\"malformed-history\":{\"outputs\":[]}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow()).toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();

            java.util.concurrent.ExecutionException failure = Assertions.assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> resultFuture.get(5, TimeUnit.SECONDS));
            Assertions.assertInstanceOf(ComfyUIException.class, failure.getCause());
            Assertions.assertTrue(failure.getCause().getMessage().contains("malformed response"));
            Assertions.assertTrue(failure.getCause().getMessage().contains("field 'outputs'"));
            // 结构错误不是历史尚未就绪，必须在第一次响应后立即失败。
            Assertions.assertEquals(1, historyRequests.get());
        }
    }

    @Test
    public void testExecutionHistoryRetryExhaustionIsClearAndBounded() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        AtomicInteger historyRequests = new AtomicInteger();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"missing-history\"}"));
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"missing-history\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/missing-history".equals(path)) {
                historyRequests.incrementAndGet();
                respondJson(exchange, 200, "{}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow()).toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();

            java.util.concurrent.ExecutionException failure = Assertions.assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> resultFuture.get(5, TimeUnit.SECONDS));
            Assertions.assertInstanceOf(ComfyUIException.class, failure.getCause());
            Assertions.assertTrue(failure.getCause().getMessage().contains("still unavailable"));
            Assertions.assertTrue(failure.getCause().getMessage().contains("10 lookup attempts"));
            Assertions.assertEquals(10, historyRequests.get());
        }
    }

    @Test
    public void testWebSocketValidatesAndEncodesConnectionUrl() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        // 底层 WebSocket 也是公开 API，必须先拒绝无效配置，并对 query 参数做严格编码。
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComfyUIWebSocket(null, "http://127.0.0.1", "client", null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComfyUIWebSocket(httpClient, "http://127.0.0.1", " ", null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComfyUIWebSocket(
                        httpClient, "http://127.0.0.1", "client", null, Duration.ZERO));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComfyUIWebSocket(
                        httpClient, "http://user:password@127.0.0.1", "client", null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComfyUIWebSocket(
                        httpClient, "http://127.0.0.1", "client", "bad\nkey", null));

        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, " HTTPS://example.com/base/// ", "client id+&=", " ", null);
        httpClient.awaitSocketCount(1);
        URI uri = httpClient.getSocket(0).getUri();
        Assertions.assertEquals("wss", uri.getScheme());
        Assertions.assertEquals("example.com", uri.getHost());
        Assertions.assertEquals("/base/ws", uri.getPath());
        Assertions.assertEquals("clientId=client%20id%2B%26%3D", uri.getRawQuery());
        httpClient.getSocket(0).open();
        webSocket.awaitReady().block(Duration.ofSeconds(5));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> webSocket.getEvents(" ").blockLast(Duration.ofSeconds(5)));
        webSocket.close();
        httpClient.getSocket(0).awaitClose();
    }

    @Test
    public void testWebSocketReplaysBufferedEventsToMultipleObservers() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "observer-client", null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        httpClient.getSocket(0).open();
        httpClient.getSocket(0).receive(event("execution_start", "{\"prompt_id\":\"observer-prompt\"}"));
        httpClient.getSocket(0).receive(event(
                "progress", "{\"prompt_id\":\"observer-prompt\",\"node\":\"1\",\"value\":1,\"max\":2}"));
        httpClient.getSocket(0).receive(event("execution_success", "{\"prompt_id\":\"observer-prompt\"}"));
        webSocket.close();

        // 两个晚订阅观察者都必须收到连接期间缓存的完整事件序列。
        List<ExecutionEvent> first = webSocket.getEvents("observer-prompt").collectList().block(Duration.ofSeconds(5));
        List<ExecutionEvent> second = webSocket.getEvents("observer-prompt").collectList().block(Duration.ofSeconds(5));
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(3, first.size());
        Assertions.assertEquals(first, second);
        Assertions.assertInstanceOf(ExecutionEvent.Started.class, first.get(0));
        Assertions.assertInstanceOf(ExecutionEvent.NodeProgress.class, first.get(1));
        Assertions.assertInstanceOf(ExecutionEvent.ExecutionSucceeded.class, first.get(2));
    }

    @Test
    public void testWebSocketFiltersMalformedProgressAndCachedNodeEntries() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "strict-events-client", null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        ControlledWebSocket socket = httpClient.getSocket(0);
        socket.open();
        CompletableFuture<List<ExecutionEvent>> eventsFuture = webSocket.getEvents("strict-prompt")
                .take(2)
                .collectList()
                .toFuture();

        socket.receive(event("progress",
                "{\"prompt_id\":\"strict-prompt\",\"node\":\"1\",\"value\":1.5,\"max\":2}"));
        socket.receive(event("progress",
                "{\"prompt_id\":\"strict-prompt\",\"node\":7,\"value\":1,\"max\":2}"));
        socket.receive(event("progress",
                "{\"prompt_id\":\"strict-prompt\",\"node\":\"1\",\"value\":1,"
                        + "\"max\":2147483648}"));
        socket.receive(event("execution_cached",
                "{\"prompt_id\":\"strict-prompt\",\"nodes\":[null,\"\",7,\"1\"]}"));
        socket.receive(event("progress",
                "{\"prompt_id\":\"strict-prompt\",\"node\":\"1\",\"value\":1,\"max\":2}"));

        List<ExecutionEvent> events = eventsFuture.get(5, TimeUnit.SECONDS);
        Assertions.assertEquals(2, events.size());
        ExecutionEvent.NodesCached cached = Assertions.assertInstanceOf(
                ExecutionEvent.NodesCached.class, events.get(0));
        Assertions.assertEquals(List.of("1"), cached.nodeIds());
        ExecutionEvent.NodeProgress progress = Assertions.assertInstanceOf(
                ExecutionEvent.NodeProgress.class, events.get(1));
        Assertions.assertEquals("1", progress.nodeId());
        Assertions.assertEquals(1, progress.current());
        Assertions.assertEquals(2, progress.total());
        webSocket.close();
        socket.awaitClose();
    }

    @Test
    public void testMalformedKnownWebSocketMessageIsTerminal() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "protocol-client", null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        httpClient.getSocket(0).open();

        // 已知终态消息缺少必需prompt_id属于协议错误，必须终止事件流而不是无限等待。
        httpClient.getSocket(0).receive(event("execution_success", "{}"));
        List<ExecutionEvent> events = webSocket.getEvents("protocol-prompt")
                .collectList()
                .block(Duration.ofSeconds(5));

        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertInstanceOf(ExecutionEvent.Error.class, events.get(0));
        Assertions.assertTrue(((ExecutionEvent.Error) events.get(0)).message().contains("missing prompt_id"));
        Assertions.assertTrue(webSocket.isConnectionTerminated());
        httpClient.getSocket(0).awaitClose();
        webSocket.close();
    }

    @Test
    public void testNonterminalExecutionMessageWithoutPromptIdIsTerminal() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "progress-protocol-client", null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        ControlledWebSocket socket = httpClient.getSocket(0);
        socket.open();

        socket.receive(event("progress", "{\"node\":\"1\",\"value\":1,\"max\":2}"));
        List<ExecutionEvent> events = webSocket.getEvents("protocol-prompt")
                .collectList()
                .block(Duration.ofSeconds(5));

        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        ExecutionEvent.Error error = Assertions.assertInstanceOf(ExecutionEvent.Error.class, events.get(0));
        Assertions.assertTrue(error.message().contains("progress message is missing prompt_id"));
        Assertions.assertTrue(webSocket.isConnectionTerminated());
        socket.awaitClose();
        webSocket.close();
    }

    @Test
    public void testWebSocketTransportFailureIsWrapped() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "transport-client", null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        httpClient.getSocket(0).open();

        // 握手后的底层异常也必须通过 SDK 异常边界交付，不能泄漏裸 IOException。
        CompletableFuture<List<ExecutionEvent>> eventsFuture = webSocket.getEvents("prompt").collectList().toFuture();
        IOException transportFailure = new IOException("connection reset");
        httpClient.getSocket(0).fail(transportFailure);
        List<ExecutionEvent> events = eventsFuture.get(5, TimeUnit.SECONDS);

        Assertions.assertEquals(1, events.size());
        ExecutionEvent.Error event = Assertions.assertInstanceOf(ExecutionEvent.Error.class, events.get(0));
        ComfyUIException failure = Assertions.assertInstanceOf(ComfyUIException.class, event.exception());
        Assertions.assertSame(transportFailure, failure.getCause());
        Assertions.assertTrue(webSocket.isConnectionTerminated());
        httpClient.getSocket(0).awaitClose();
        webSocket.close();
    }

    @Test
    public void testUnexpectedConnectionCloseIsTerminal() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        startServer(exchange -> {
            if ("/prompt".equals(exchange.getRequestURI().getRawPath())) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId).serverClose(1011, "server failure");
                respondJson(exchange, 200, "{\"prompt_id\":\"closed-prompt\",\"number\":1,\"node_errors\":{}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<List<ExecutionEvent>> eventsFuture = client.executeWithEvents(workflow()).collectList().toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();

            List<ExecutionEvent> events = eventsFuture.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(1, events.size());
            Assertions.assertInstanceOf(ExecutionEvent.ConnectionClosed.class, events.get(0));
            Assertions.assertTrue(((ExecutionEvent.ConnectionClosed) events.get(0)).message().contains("server failure"));
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testClientCloseTerminatesSocketWaitingForReadiness() throws Exception {
        AtomicInteger promptRequests = new AtomicInteger();
        startServer(exchange -> {
            promptRequests.incrementAndGet();
            respondJson(exchange, 500, "{\"error\":\"unexpected request\"}");
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ComfyUIClient client = createClient(httpClient);
        CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow()).toFuture();
        httpClient.awaitSocketCount(1);
        ControlledWebSocket socket = httpClient.getSocket(0);

        // close需要让等待ready的执行失败，且迟到open必须直接abort而不是继续握手。
        client.close();
        Assertions.assertThrows(
                java.util.concurrent.ExecutionException.class,
                () -> resultFuture.get(5, TimeUnit.SECONDS));
        socket.open();
        Assertions.assertEquals(0, promptRequests.get());
        Assertions.assertTrue(socket.awaitAbort(Duration.ofSeconds(2)));
        Assertions.assertEquals(1, socket.getAbortCalls());
        Assertions.assertEquals(0, socket.getCloseCalls());
        Assertions.assertTrue(socket.isInputClosed() && socket.isOutputClosed());
    }

    @Test
    public void testExecutionUploadsSequentiallyAndSubmitsReturnedFilePaths() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        AtomicInteger uploadResponses = new AtomicInteger();
        List<byte[]> uploadedBodies = Collections.synchronizedList(new ArrayList<>());
        AtomicReference<JsonNode> submittedPrompt = new AtomicReference<>();
        AtomicReference<ExecutionResult> listenerResult = new AtomicReference<>();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/upload/image".equals(path)) {
                uploadedBodies.add(exchange.getRequestBody().readAllBytes());
                int number = uploadResponses.incrementAndGet();
                String subfolder = number == 1 ? "nested" : "";
                respondJson(exchange, 200,
                        "{\"name\":\"renamed-" + number + ".png\",\"subfolder\":\"" + subfolder + "\"}");
            } else if ("/prompt".equals(path)) {
                JsonNode body = JacksonUtils.readTree(readBody(exchange));
                submittedPrompt.set(body.path("prompt"));
                String clientId = body.path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"upload-prompt\"}"));
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"upload-prompt\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/upload-prompt".equals(path)) {
                respondJson(exchange, 200, "{\"upload-prompt\":{\"outputs\":{}}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClient.gateFirstUpload();
        httpClientRef.set(httpClient);
        Workflow workflow = Workflow.fromApiJson("""
                {
                  "1": {"class_type": "LoadImage", "inputs": {}},
                  "2": {"class_type": "LoadImage", "inputs": {}}
                }
                """);
        byte[] firstData = "first-original".getBytes(StandardCharsets.UTF_8);
        List<InputFile> inputFiles = new ArrayList<>(List.of(
                InputFile.image("first.png", firstData, "image/png"),
                InputFile.image("second.png", new byte[]{2}, "image/png")));
        List<String> fileNodeIds = new ArrayList<>(List.of("1", "2"));
        ExecutionOptions options = ExecutionOptions.builder()
                .inputFiles(inputFiles)
                .fileNodeIds(fileNodeIds)
                .listener(new ExecutionListener() {
                    @Override
                    public void onComplete(ExecutionResult result) {
                        listenerResult.set(result);
                    }
                })
                .build();

        try (ComfyUIClient client = createClient(httpClient)) {
            Mono<ExecutionResult> execution = client.execute(workflow, options);
            // execute组装后修改调用方数据，证明订阅时仍使用已验证的深快照。
            Arrays.fill(firstData, (byte) 'x');
            workflow.setProperty("2", "caller_mutation", true);
            inputFiles.clear();
            fileNodeIds.clear();
            CompletableFuture<ExecutionResult> resultFuture = execution.toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();
            httpClient.awaitUploadRequestCount(1);
            // 第一个上传响应被门控时，第二个请求尚未发送，证明执行准备是顺序的。
            Assertions.assertEquals(1, httpClient.getUploadRequests());
            httpClient.releaseFirstUpload();
            httpClient.awaitUploadRequestCount(2);

            Assertions.assertEquals("upload-prompt", resultFuture.get(5, TimeUnit.SECONDS).getPromptId());
            Assertions.assertEquals("upload-prompt", listenerResult.get().getPromptId());
            Assertions.assertEquals(
                    "nested/renamed-1.png",
                    submittedPrompt.get().path("1").path("inputs").path("image").asText());
            Assertions.assertEquals(
                    "renamed-2.png",
                    submittedPrompt.get().path("2").path("inputs").path("image").asText());
            Assertions.assertFalse(
                    submittedPrompt.get().path("2").path("inputs").has("caller_mutation"));
            Assertions.assertEquals(2, uploadedBodies.size());
            Assertions.assertTrue(
                    new String(uploadedBodies.get(0), StandardCharsets.UTF_8).contains("first-original"));
        }
    }

    @Test
    public void testListenerFailurePropagatesWithoutReenteringErrorCallback() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"listener-prompt\"}"));
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"listener-prompt\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/listener-prompt".equals(path)) {
                respondJson(exchange, 200, "{\"listener-prompt\":{\"outputs\":{}}}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);
        NoSuchElementException expected = new NoSuchElementException("listener failed");
        AtomicInteger errorCallbacks = new AtomicInteger();
        ExecutionOptions options = ExecutionOptions.builder()
                .listener(new ExecutionListener() {
                    @Override
                    public void onComplete(ExecutionResult result) {
                        throw expected;
                    }

                    @Override
                    public void onError(String message, Exception exception) {
                        errorCallbacks.incrementAndGet();
                    }
                })
                .build();

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow(), options).toFuture();
            httpClient.awaitSocketCount(1);
            ControlledWebSocket socket = httpClient.getSocket(0);
            socket.open();

            java.util.concurrent.ExecutionException failure = Assertions.assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> resultFuture.get(5, TimeUnit.SECONDS));
            Assertions.assertSame(expected, failure.getCause());
            Assertions.assertEquals(0, errorCallbacks.get());
            socket.awaitClose();
        }
    }

    @Test
    public void testSocketTerminationDuringPreparationPreventsPromptSubmission() throws Exception {
        AtomicInteger promptRequests = new AtomicInteger();
        startServer(exchange -> {
            if ("/upload/image".equals(exchange.getRequestURI().getRawPath())) {
                respondJson(exchange, 200, "{\"name\":\"input.png\",\"subfolder\":\"\"}");
            } else if ("/prompt".equals(exchange.getRequestURI().getRawPath())) {
                promptRequests.incrementAndGet();
                respondJson(exchange, 500, "{\"error\":\"prompt must not be submitted\"}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClient.gateFirstUpload();
        Workflow workflow = Workflow.fromApiJson("""
                {"1":{"class_type":"LoadImage","inputs":{}}}
                """);
        ExecutionOptions options = ExecutionOptions.builder()
                .inputFiles(List.of(InputFile.image("input.png", new byte[]{1}, "image/png")))
                .fileNodeIds(List.of("1"))
                .build();

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow, options).toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();
            httpClient.awaitUploadRequestCount(1);
            // 上传准备被门控时关闭连接；释放上传后，执行必须本地失败而非提交/prompt。
            httpClient.getSocket(0).serverClose(1011, "connection lost during preparation");
            httpClient.releaseFirstUpload();

            java.util.concurrent.ExecutionException failure = Assertions.assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> resultFuture.get(5, TimeUnit.SECONDS));
            Assertions.assertInstanceOf(ComfyUIException.class, failure.getCause());
            Assertions.assertTrue(failure.getCause().getMessage().contains("terminated before workflow submission"));
            Assertions.assertEquals(0, promptRequests.get());
        }
    }

    @Test
    public void testMismatchedExecutionFilesFailBeforeOpeningWebSocket() throws IOException {
        startServer(exchange -> respondJson(exchange, 500, "{\"error\":\"unexpected request\"}"));
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        ExecutionOptions options = ExecutionOptions.builder()
                .inputFiles(List.of(InputFile.image("input.png", new byte[]{1}, "image/png")))
                .fileNodeIds(List.of())
                .build();

        try (ComfyUIClient client = createClient(httpClient)) {
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> client.executeWithEvents(workflow(), options).collectList().block(Duration.ofSeconds(5)));
            Assertions.assertEquals(0, httpClient.getSocketCount());
        }
    }

    @Test
    public void testExecutionMapsPluralVideosAndSkipsMalformedMediaEntries() throws Exception {
        AtomicReference<ControlledWebSocketHttpClient> httpClientRef = new AtomicReference<>();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                String clientId = JacksonUtils.readTree(readBody(exchange)).path("client_id").asText();
                httpClientRef.get().socketForClientId(clientId)
                        .receive(event("execution_success", "{\"prompt_id\":\"video-prompt\"}"));
                respondJson(exchange, 200, "{\"prompt_id\":\"video-prompt\",\"number\":1,\"node_errors\":{}}");
            } else if ("/history/video-prompt".equals(path)) {
                respondJson(exchange, 200, """
                        {"video-prompt":{"outputs":{"1":{"video":[{"filename":"legacy.mp4"}],
                        "videos":[null,"invalid",{}, {"filename":""},
                        {"filename":"bad-subfolder.mp4","subfolder":[]},
                        {"filename":"bad-type.mp4","type":{}},
                        {"filename":"blank-type.mp4","type":" "},
                        {"filename":"current.mp4","subfolder":"clips"}]}}}}
                        """);
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClientRef.set(httpClient);

        try (ComfyUIClient client = createClient(httpClient)) {
            CompletableFuture<ExecutionResult> resultFuture = client.execute(workflow()).toFuture();
            httpClient.awaitSocketCount(1);
            httpClient.getSocket(0).open();

            // 新旧视频字段均映射为视频；畸形、非对象或缺少文件名的条目不得生成OutputFile。
            ExecutionResult result = resultFuture.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(List.of("legacy.mp4", "current.mp4"),
                    result.getVideos().stream().map(output -> output.getFilename()).toList());
            var nodeOutput = result.getNodeOutput("1").orElseThrow();
            Assertions.assertTrue(nodeOutput.hasVideos());
            Assertions.assertEquals(2, nodeOutput.getVideos().size());
            Assertions.assertEquals(8, nodeOutput.getData().path("videos").size());
        }
    }

    @Test
    public void testInvalidExecutionBindingsAndTimeoutFailBeforeTransportSetup() throws IOException {
        startServer(exchange -> respondJson(exchange, 500, "{\"error\":\"unexpected request\"}"));
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        Workflow imageWorkflow = Workflow.fromApiJson("""
                {"1":{"class_type":"LoadImage","inputs":{}}}
                """);

        try (ComfyUIClient client = createClient(httpClient)) {
            // 无效文件、节点绑定和执行超时必须在创建WebSocket或上传HTTP请求前失败。
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.executeWithEvents(imageWorkflow, ExecutionOptions.builder()
                            .inputFiles(Collections.singletonList(null))
                            .fileNodeIds(List.of("1"))
                            .build()).collectList().block(Duration.ofSeconds(5)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.executeWithEvents(imageWorkflow, ExecutionOptions.builder()
                            .inputFiles(List.of(InputFile.audio("input.wav", new byte[]{1}, "audio/wav")))
                            .fileNodeIds(List.of("1"))
                            .build()).collectList().block(Duration.ofSeconds(5)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.executeWithEvents(imageWorkflow, ExecutionOptions.builder()
                            .inputFiles(List.of(InputFile.image("input.png", new byte[]{1}, "image/png")))
                            .fileNodeIds(List.of(" "))
                            .build()).collectList().block(Duration.ofSeconds(5)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.executeWithEvents(workflow(), ExecutionOptions.builder()
                            .timeout(Duration.ZERO)
                            .build()).collectList().block(Duration.ofSeconds(5)));

            Assertions.assertEquals(0, httpClient.getSocketCount());
            Assertions.assertEquals(0, httpClient.getUploadRequests());
        }
    }

    /**
     * 验证 ExecutionOptions.timeout 是从订阅起算的绝对执行期限，而不是相邻事件空闲超时：
     * 当服务端持续以小于 timeout 的间隔发送进度事件时，总历时超过 timeout 后
     * 执行必须以 ExecutionException 终止；错误信息必须包含 timeout；
     * 同时 ExecutionListener.onError 必须收到同一执行超时异常实例。
     */
    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testExecutionTimeoutIsAbsoluteNotEventIdle() throws Exception {
        AtomicInteger promptRequests = new AtomicInteger();
        CountDownLatch promptSubmitted = new CountDownLatch(1);
        startServer(exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            if ("/prompt".equals(path)) {
                promptRequests.incrementAndGet();
                respondJson(exchange, 200,
                        "{\"prompt_id\":\"timeout-prompt\",\"number\":1,\"node_errors\":{}}");
                promptSubmitted.countDown();
            } else {
                respondJson(exchange, 404, "{\"error\":\"not found\"}");
            }
        });
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();

        Duration timeout = Duration.ofMillis(1500);
        // 进度事件间隔必须远小于 timeout；否则会被误判为事件空闲超时而无法证明绝对期限。
        Duration progressInterval = Duration.ofMillis(50);
        CountDownLatch firstProgressObserved = new CountDownLatch(1);
        AtomicInteger progressEvents = new AtomicInteger();
        AtomicLong previousProgressNanos = new AtomicLong();
        AtomicLong maxProgressGapNanos = new AtomicLong();
        AtomicLong lastProgressNanos = new AtomicLong();
        AtomicInteger listenerErrorCount = new AtomicInteger();
        AtomicReference<String> listenerMessage = new AtomicReference<>();
        AtomicReference<Exception> listenerException = new AtomicReference<>();
        ExecutionListener listener = new ExecutionListener() {
            @Override
            public void onNodeProgress(String nodeId, int current, int total) {
                progressEvents.incrementAndGet();
                long now = System.nanoTime();
                long previous = previousProgressNanos.getAndSet(now);
                if (previous != 0) {
                    maxProgressGapNanos.accumulateAndGet(now - previous, Math::max);
                }
                lastProgressNanos.set(now);
                firstProgressObserved.countDown();
            }

            @Override
            public void onError(String message, Exception exception) {
                listenerErrorCount.incrementAndGet();
                listenerMessage.set(message);
                listenerException.set(exception);
            }
        };
        ExecutionOptions options = ExecutionOptions.builder()
                .timeout(timeout)
                .listener(listener)
                .build();

        try (ComfyUIClient client = createClient(httpClient)) {
            long subscriptionStartNanos = System.nanoTime();
            CompletableFuture<ExecutionResult> resultFuture =
                    client.execute(workflow(), options).toFuture();
            httpClient.awaitSocketCount(1);
            ControlledWebSocket socket = httpClient.getSocket(0);
            socket.open();
            Assertions.assertTrue(promptSubmitted.await(5, TimeUnit.SECONDS),
                    "prompt submission did not complete before the bounded wait expired");
            Assertions.assertEquals(1, promptRequests.get());

            CountDownLatch stopProducer = new CountDownLatch(1);
            Thread producer = new Thread(() -> {
                int step = 0;
                while (stopProducer.getCount() != 0) {
                    step++;
                    socket.receive(event(
                            "progress",
                            "{\"prompt_id\":\"timeout-prompt\",\"node\":\"1\","
                                    + "\"value\":" + step + ",\"max\":1000000}"));
                    try {
                        if (stopProducer.await(progressInterval.toMillis(), TimeUnit.MILLISECONDS)) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }, "timeout-progress-producer");
            producer.setDaemon(true);
            producer.start();

            Assertions.assertTrue(firstProgressObserved.await(2, TimeUnit.SECONDS),
                    "no progress event reached the execution listener before timeout");

            java.util.concurrent.ExecutionException failure;
            long failureObservedNanos;
            try {
                failure = Assertions.assertThrows(
                        java.util.concurrent.ExecutionException.class,
                        () -> resultFuture.get(5, TimeUnit.SECONDS));
                failureObservedNanos = System.nanoTime();
            } finally {
                stopProducer.countDown();
                producer.interrupt();
                producer.join(2000);
            }
            Assertions.assertFalse(producer.isAlive(),
                    "progress producer did not stop within the bounded join");

            long elapsedNanos = failureObservedNanos - subscriptionStartNanos;

            // 1) 必须以 ExecutionException 终止，而不是空闲超时。
            ExecutionException timeoutFailure = Assertions.assertInstanceOf(
                    ExecutionException.class, failure.getCause());
            String errorMessage = timeoutFailure.getMessage();
            Assertions.assertNotNull(errorMessage);
            Assertions.assertTrue(errorMessage.contains("timeout"),
                    "expected timeout-related message but got: " + errorMessage);
            Assertions.assertTrue(elapsedNanos >= timeout.toNanos(),
                    "execution ended before the subscription-relative deadline: "
                            + TimeUnit.NANOSECONDS.toMillis(elapsedNanos) + "ms");

            // 2) 超时前持续收到进度，且最后一个进度距错误远小于 timeout，排除事件空闲超时。
            Assertions.assertTrue(progressEvents.get() >= 5,
                    "too few progress events to prove a continuously active stream: "
                            + progressEvents.get());
            Assertions.assertTrue(maxProgressGapNanos.get() < timeout.toNanos(),
                    "observed progress gap reached the configured timeout: gap="
                            + TimeUnit.NANOSECONDS.toMillis(maxProgressGapNanos.get()) + "ms");
            long finalIdleNanos = failureObservedNanos - lastProgressNanos.get();
            Assertions.assertTrue(finalIdleNanos >= 0 && finalIdleNanos < timeout.toNanos(),
                    "execution timed out only after an idle gap: idle="
                            + TimeUnit.NANOSECONDS.toMillis(finalIdleNanos) + "ms");

            // 3) ExecutionListener.onError 必须收到同一执行超时异常。
            Assertions.assertEquals(1, listenerErrorCount.get(),
                    "ExecutionListener.onError must be invoked exactly once");
            Assertions.assertEquals(errorMessage, listenerMessage.get());
            Assertions.assertSame(timeoutFailure, listenerException.get(),
                    "listener and subscriber must receive the same timeout exception instance");
            Assertions.assertTrue(resultFuture.isCompletedExceptionally());
        }
    }

    /**
     * 验证 ComfyUIWebSocket.close() 在握手 future 尚未完成时必须取消底层握手：
     * WebSocketClientUtils.connect 在 connectFuture 被取消时必须将底层握手 future 标记为
     * cancelled（命中 whenComplete 回调）；若连接在取消之后迟到打开，
     * wsListener.onOpen 必须命中 future.isCancelled() 分支并 abort 底层 socket，
     * 不得让连接泄漏到应用代码。
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testWebSocketCloseCancelsPendingHandshake() throws Exception {
        ControlledWebSocketHttpClient httpClient = new ControlledWebSocketHttpClient();
        httpClient.usePendingHandshake();
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient, "http://127.0.0.1", "pending-handshake-client",
                null, Duration.ofSeconds(1));
        httpClient.awaitSocketCount(1);
        ControlledWebSocket pendingSocket = httpClient.getSocket(0);
        CompletableFuture<WebSocket> handshakeFuture = pendingSocket.getHandshakeFuture();
        Assertions.assertNotNull(handshakeFuture,
                "pending handshake mode must expose the handshake future");
        Assertions.assertFalse(handshakeFuture.isDone(),
                "handshake future should be pending before close()");

        // 握手尚未完成时 close，必须命中 connectFuture.cancel(true) -> 握手取消 的路径。
        webSocket.close();

        Assertions.assertTrue(webSocket.isClosed(),
                "ComfyUIWebSocket.close() must mark itself closed");
        Assertions.assertTrue(pendingSocket.awaitHandshakeCancelled(Duration.ofSeconds(2)),
                "underlying handshake future should be cancelled when connectFuture is cancelled");
        Assertions.assertTrue(handshakeFuture.isCancelled());

        // 连接迟到打开：底层 socket 必须被 abort 而非泄漏。
        Assertions.assertEquals(0, pendingSocket.getAbortCalls());
        pendingSocket.completeHandshake();
        Assertions.assertTrue(pendingSocket.awaitAbort(Duration.ofSeconds(2)),
                "late-opened socket must be aborted by the close path");
        Assertions.assertEquals(1, pendingSocket.getAbortCalls(),
                "abort() must be called exactly once on the late-opened socket");
        Assertions.assertEquals(0, pendingSocket.getCloseCalls(),
                "a cancelled late open must use abort(), not a graceful close handshake");
        Assertions.assertTrue(pendingSocket.isInputClosed() && pendingSocket.isOutputClosed(),
                "underlying socket must be fully closed after abort");
        // 已关闭的 ComfyUIWebSocket 不得因迟到事件被标记为可用。
        Assertions.assertFalse(webSocket.isConnectionTerminated(),
                "late handshake must not leak events into ComfyUIWebSocket after close()");
    }

    private void startServer(HttpHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        serverExecutor = Executors.newCachedThreadPool();
        server.setExecutor(serverExecutor);
        server.createContext("/", handler);
        server.start();
    }

    private ComfyUIClient createClient(HttpClient httpClient) {
        return new ComfyUIClientFactory(httpClient).create(ComfyUIClientOptions.builder()
                .baseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/")
                .httpClient(httpClient)
                .build());
    }

    private Workflow workflow() {
        return Workflow.fromApiJson("{\"1\":{\"class_type\":\"TestNode\",\"inputs\":{}}}");
    }

    private String event(String type, String data) {
        return "{\"type\":\"" + type + "\",\"data\":" + data + "}";
    }

    private boolean await(CountDownLatch latch) {
        try {
            return latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void respondJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static class ControlledWebSocketHttpClient extends HttpClient {

        private final HttpClient delegate = HttpClient.newHttpClient();
        private final List<ControlledWebSocket> sockets = new ArrayList<>();
        private final Map<String, ControlledWebSocket> socketsByClientId = new ConcurrentHashMap<>();
        private final CompletableFuture<Void> firstUploadRelease = new CompletableFuture<>();
        private boolean gateFirstUpload;
        private boolean pendingHandshakeMode;
        private int uploadRequests;

        public synchronized void gateFirstUpload() {
            gateFirstUpload = true;
        }

        /**
         * 让后续 buildAsync 返回尚未完成的握手 future，由测试主动调用
         * {@link ControlledWebSocket#completeHandshake()} 完成握手。
         * 用于验证 connectFuture 取消路径与迟到打开的 abort 行为。
         */
        public synchronized void usePendingHandshake() {
            this.pendingHandshakeMode = true;
        }

        public void releaseFirstUpload() {
            firstUploadRelease.complete(null);
        }

        public synchronized void awaitUploadRequestCount(int expected) throws InterruptedException, TimeoutException {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (uploadRequests < expected) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    throw new TimeoutException("Timed out waiting for " + expected + " upload requests");
                }
                TimeUnit.NANOSECONDS.timedWait(this, remaining);
            }
        }

        public synchronized int getUploadRequests() {
            return uploadRequests;
        }

        public synchronized int getSocketCount() {
            return sockets.size();
        }

        public synchronized ControlledWebSocket getSocket(int index) {
            return sockets.get(index);
        }

        public ControlledWebSocket socketForClientId(String clientId) {
            return socketsByClientId.get(clientId);
        }

        public synchronized int indexOfClientId(String clientId) {
            for (int i = 0; i < sockets.size(); i++) {
                if (sockets.get(i).getClientId().equals(clientId)) {
                    return i;
                }
            }
            return -1;
        }

        public synchronized void awaitSocketCount(int expected) throws InterruptedException, TimeoutException {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (sockets.size() < expected) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    throw new TimeoutException("Timed out waiting for " + expected + " WebSocket connections");
                }
                TimeUnit.NANOSECONDS.timedWait(this, remaining);
            }
        }

        @Override
        public WebSocket.Builder newWebSocketBuilder() {
            return new WebSocket.Builder() {
                @Override
                public WebSocket.Builder header(String name, String value) {
                    return this;
                }

                @Override
                public WebSocket.Builder connectTimeout(Duration timeout) {
                    return this;
                }

                @Override
                public WebSocket.Builder subprotocols(String mostPreferred, String... lesserPreferred) {
                    return this;
                }

                @Override
                public CompletableFuture<WebSocket> buildAsync(URI uri, WebSocket.Listener listener) {
                    ControlledWebSocket socket = new ControlledWebSocket(uri, listener);
                    synchronized (ControlledWebSocketHttpClient.this) {
                        sockets.add(socket);
                        socketsByClientId.put(socket.getClientId(), socket);
                        ControlledWebSocketHttpClient.this.notifyAll();
                    }
                    if (pendingHandshakeMode) {
                        return socket.createPendingHandshake();
                    }
                    return CompletableFuture.completedFuture(socket);
                }
            };
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return delegate.cookieHandler();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return delegate.connectTimeout();
        }

        @Override
        public Redirect followRedirects() {
            return delegate.followRedirects();
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return delegate.proxy();
        }

        @Override
        public SSLContext sslContext() {
            return delegate.sslContext();
        }

        @Override
        public SSLParameters sslParameters() {
            return delegate.sslParameters();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return delegate.authenticator();
        }

        @Override
        public Version version() {
            return delegate.version();
        }

        @Override
        public Optional<Executor> executor() {
            return delegate.executor();
        }

        @Override
        public <T> HttpResponse<T> send(
                HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException {
            return delegate.send(request, responseBodyHandler);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            CompletableFuture<HttpResponse<T>> response = delegate.sendAsync(request, responseBodyHandler);
            if (!"/upload/image".equals(request.uri().getPath())) {
                return response;
            }
            int requestNumber;
            boolean gate;
            synchronized (this) {
                requestNumber = ++uploadRequests;
                gate = gateFirstUpload && requestNumber == 1;
                notifyAll();
            }
            return gate ? firstUploadRelease.thenCompose(ignored -> response) : response;
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return delegate.sendAsync(request, responseBodyHandler, pushPromiseHandler);
        }
    }

    private static class ControlledWebSocket implements WebSocket {

        private final URI uri;
        private final Listener listener;
        private final AtomicInteger closeCalls = new AtomicInteger();
        private final AtomicInteger abortCalls = new AtomicInteger();
        private volatile boolean inputClosed;
        private volatile boolean outputClosed;
        private volatile CompletableFuture<WebSocket> handshakeFuture;

        ControlledWebSocket(URI uri, Listener listener) {
            this.uri = uri;
            this.listener = listener;
        }

        public URI getUri() {
            return uri;
        }

        public String getClientId() {
            String query = uri.getRawQuery();
            return query.substring(query.indexOf('=') + 1);
        }

        public synchronized void awaitClose() throws InterruptedException, TimeoutException {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (closeCalls.get() == 0) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    throw new TimeoutException("Timed out waiting for WebSocket close");
                }
                TimeUnit.NANOSECONDS.timedWait(this, remaining);
            }
        }

        /**
         * 创建一个尚未完成的握手 future。该 future 仅在
         * {@link #completeHandshake()} 被调用时才会完成，
         * 以模拟尚未完成的 WebSocket 握手。
         */
        public synchronized CompletableFuture<WebSocket> createPendingHandshake() {
            handshakeFuture = new CompletableFuture<>();
            return handshakeFuture;
        }

        /**
         * 在 pending 握手模式下模拟迟到的底层 open 回调。JDK 会先触发
         * {@link WebSocket.Listener#onOpen(WebSocket)} 再完成 buildAsync future；
         * 因此即使 future 已被取消，也必须独立交付迟到的 onOpen。
         */
        public synchronized void completeHandshake() {
            CompletableFuture<WebSocket> future = handshakeFuture;
            if (future == null) {
                throw new IllegalStateException(
                        "completeHandshake() requires createPendingHandshake() to be called first");
            }
            listener.onOpen(this);
            future.complete(this);
        }

        public synchronized CompletableFuture<WebSocket> getHandshakeFuture() {
            return handshakeFuture;
        }

        /**
         * 等待握手 future 被取消，最多等待指定时长。
         *
         * @return true 当握手 future 在等待期内被取消
         */
        public synchronized boolean awaitHandshakeCancelled(Duration timeout)
                throws InterruptedException {
            long deadline = System.nanoTime() + timeout.toNanos();
            while (handshakeFuture == null || !handshakeFuture.isCancelled()) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    return false;
                }
                TimeUnit.NANOSECONDS.timedWait(
                        this, Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(5)));
            }
            return true;
        }

        /**
         * 等待底层 socket 被 abort()，最多等待指定时长。
         *
         * @return true 当 abort() 在等待期内被调用
         */
        public synchronized boolean awaitAbort(Duration timeout) throws InterruptedException {
            long deadline = System.nanoTime() + timeout.toNanos();
            while (abortCalls.get() == 0) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    return false;
                }
                TimeUnit.NANOSECONDS.timedWait(
                        this, Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(5)));
            }
            return true;
        }

        public int getAbortCalls() {
            return abortCalls.get();
        }

        public int getCloseCalls() {
            return closeCalls.get();
        }

        public void open() {
            listener.onOpen(this);
        }

        public void receive(String message) {
            listener.onText(this, message, true);
        }

        public void serverClose(int statusCode, String reason) {
            inputClosed = true;
            outputClosed = true;
            listener.onClose(this, statusCode, reason);
        }

        public void fail(Throwable failure) {
            listener.onError(this, failure);
        }

        @Override
        public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public synchronized CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
            closeCalls.incrementAndGet();
            outputClosed = true;
            inputClosed = true;
            listener.onClose(this, statusCode, reason);
            notifyAll();
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public void request(long n) {
        }

        @Override
        public String getSubprotocol() {
            return "";
        }

        @Override
        public boolean isOutputClosed() {
            return outputClosed;
        }

        @Override
        public boolean isInputClosed() {
            return inputClosed;
        }

        @Override
        public synchronized void abort() {
            abortCalls.incrementAndGet();
            outputClosed = true;
            inputClosed = true;
            notifyAll();
        }
    }
}
