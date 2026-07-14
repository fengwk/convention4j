package fun.fengwk.convention4j.comfyui;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fun.fengwk.convention4j.comfyui.exception.ComfyUIException;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 无状态客户端API测试
 *
 * @author fengwk
 */
public class ComfyUIClientStatelessTest {

    private HttpServer server;

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testSubmitMappingAuthorizationAndLazyWebSocket() throws IOException {
        AtomicReference<CapturedRequest> captured = new AtomicReference<>();
        startServer(exchange -> {
            captured.set(capture(exchange));
            respondJson(exchange, 200, """
                    {
                      "prompt_id": "prompt-1",
                      "number": 7.5,
                      "node_errors": {"2": {"errors": []}}
                    }
                    """);
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, "secret-key")) {
            // 提交结果和请求体同时断言，证明公开模型与当前 /prompt 合约一致。
            PromptSubmission submission = client.submit(workflow()).block(Duration.ofSeconds(5));

            Assertions.assertNotNull(submission);
            Assertions.assertEquals("prompt-1", submission.getPromptId());
            Assertions.assertEquals(7.5, submission.getNumber());
            Assertions.assertTrue(submission.getNodeErrors().has("2"));

            CapturedRequest request = captured.get();
            Assertions.assertNotNull(request);
            Assertions.assertEquals("POST", request.method());
            Assertions.assertEquals("/prompt", request.rawPath());
            Assertions.assertEquals("Bearer secret-key", request.authorization());
            JsonNode requestBody = JacksonUtils.readTree(request.body());
            Assertions.assertFalse(requestBody.path("client_id").asText().isBlank());
            Assertions.assertEquals("TestNode", requestBody.path("prompt").path("1").path("class_type").asText());
            Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        }

        Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
    }

    @Test
    public void testGetJobMappingAndEncodedPath() throws IOException {
        AtomicReference<CapturedRequest> captured = new AtomicReference<>();
        startServer(exchange -> {
            captured.set(capture(exchange));
            respondJson(exchange, 200, """
                    {
                      "id": "job/id +?",
                      "status": "paused_future",
                      "priority": 4.5,
                      "create_time": 1716200000000,
                      "update_time": 1716200100000,
                      "workflow_id": "workflow-1",
                      "execution_start_time": 1716200005000,
                      "execution_end_time": 1716200099000,
                      "outputs_count": 3,
                      "outputs": {"9": {"images": [{"filename": "out.png"}]}},
                      "execution_error": {"node_id": "4", "message": "failed"},
                      "execution_status": {"status_str": "error"},
                      "workflow": {"prompt": {"1": {"class_type": "TestNode"}}},
                      "preview_output": {"filename": "out.png"}
                    }
                    """);
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // 使用未知状态字符串，证明状态映射不会被客户端枚举限制。
            ComfyUIJob job = client.getJob("job/id +?").block(Duration.ofSeconds(5));

            Assertions.assertNotNull(job);
            Assertions.assertEquals("job/id +?", job.getId());
            Assertions.assertEquals("paused_future", job.getStatus());
            Assertions.assertEquals(4.5, job.getPriority());
            Assertions.assertEquals(1716200000000L, job.getCreateTime());
            Assertions.assertEquals(1716200100000L, job.getUpdateTime());
            Assertions.assertEquals("workflow-1", job.getWorkflowId());
            Assertions.assertEquals(1716200005000L, job.getExecutionStartTime());
            Assertions.assertEquals(1716200099000L, job.getExecutionEndTime());
            Assertions.assertEquals(3, job.getOutputsCount());
            Assertions.assertEquals("out.png", job.getOutputs().path("9").path("images").path(0).path("filename").asText());
            Assertions.assertEquals("failed", job.getExecutionError().path("message").asText());
            Assertions.assertEquals("error", job.getExecutionStatus().path("status_str").asText());
            Assertions.assertEquals("TestNode", job.getWorkflow().path("prompt").path("1").path("class_type").asText());
            Assertions.assertEquals("out.png", job.getPreviewOutput().path("filename").asText());

            Assertions.assertEquals("GET", captured.get().method());
            Assertions.assertEquals("/api/jobs/job%2Fid%20%2B%3F", captured.get().rawPath());
            Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        }
    }

    @Test
    public void testGetPendingJobWithoutUpdateTime() throws IOException {
        startServer(exchange -> respondJson(exchange, 200, """
                {
                  "id": "pending-job",
                  "status": "pending",
                  "priority": 12,
                  "create_time": 1716200000000,
                  "outputs_count": 0,
                  "workflow_id": "workflow-pending"
                }
                """));
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // 精确复现 normalize_queue_item 响应：没有 update_time 和终态执行字段。
            ComfyUIJob job = client.getJob("pending-job").block(Duration.ofSeconds(5));

            Assertions.assertNotNull(job);
            Assertions.assertEquals("pending", job.getStatus());
            Assertions.assertEquals(12.0, job.getPriority());
            Assertions.assertEquals(1716200000000L, job.getCreateTime());
            Assertions.assertNull(job.getUpdateTime());
            Assertions.assertEquals("workflow-pending", job.getWorkflowId());
            Assertions.assertNull(job.getExecutionStartTime());
            Assertions.assertNull(job.getExecutionEndTime());
            Assertions.assertEquals(0, job.getOutputsCount());
            Assertions.assertNull(job.getOutputs());
        }
    }

    @Test
    public void testCancelJobMapping() throws IOException {
        AtomicReference<CapturedRequest> captured = new AtomicReference<>();
        startServer(exchange -> {
            captured.set(capture(exchange));
            respondJson(exchange, 200, "{\"cancelled\":false}");
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // false 是服务端定义的幂等取消结果，不能被当作请求失败。
            Boolean cancelled = client.cancelJob("already-done").block(Duration.ofSeconds(5));

            Assertions.assertEquals(Boolean.FALSE, cancelled);
            Assertions.assertEquals("POST", captured.get().method());
            Assertions.assertEquals("/api/jobs/already-done/cancel", captured.get().rawPath());
            Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        }
    }

    @Test
    public void testErrorsAndValidation() throws IOException {
        AtomicInteger requestCount = new AtomicInteger();
        startServer(exchange -> {
            requestCount.incrementAndGet();
            if (exchange.getRequestURI().getRawPath().endsWith("/cancel")) {
                respondJson(exchange, 200, "{\"cancelled\":\"yes\"}");
            } else {
                respondJson(exchange, 404, "{\"error\":\"Job not found\"}");
            }
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // 非2xx错误包含操作、状态和服务端正文，便于定位请求失败。
            ComfyUIException notFound = Assertions.assertThrows(
                    ComfyUIException.class,
                    () -> client.getJob("missing").block(Duration.ofSeconds(5)));
            Assertions.assertTrue(notFound.getMessage().contains("Get job missing"));
            Assertions.assertTrue(notFound.getMessage().contains("HTTP 404"));
            Assertions.assertTrue(notFound.getMessage().contains("Job not found"));

            // 字段类型错误必须显式失败，避免把 malformed body 静默映射成 false。
            ComfyUIException malformed = Assertions.assertThrows(
                    ComfyUIException.class,
                    () -> client.cancelJob("bad").block(Duration.ofSeconds(5)));
            Assertions.assertTrue(malformed.getMessage().contains("malformed response"));
            Assertions.assertTrue(malformed.getMessage().contains("cancelled"));

            int requestsBeforeValidation = requestCount.get();
            Assertions.assertThrows(IllegalArgumentException.class, () -> client.submit(null).block());
            Assertions.assertThrows(IllegalArgumentException.class, () -> client.getJob("  ").block());
            Assertions.assertThrows(IllegalArgumentException.class, () -> client.cancelJob(null).block());
            Assertions.assertThrows(IllegalArgumentException.class, () -> client.getHistory(" ").block());
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.getFile(" ", null, null).block());
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.uploadFile("bad\"name.png", new byte[0], "image/png").block());
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.uploadFile("name.png", null, "image/png").block());
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> client.uploadFile("name.png", new byte[0], null).block());
            Assertions.assertEquals(requestsBeforeValidation, requestCount.get());
            Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        }
    }

    @Test
    public void testHistoryStatusMissingEntryAndEncodedPath() throws IOException {
        AtomicReference<CapturedRequest> captured = new AtomicReference<>();
        AtomicInteger missingEntryRequests = new AtomicInteger();
        startServer(exchange -> {
            captured.set(capture(exchange));
            String path = exchange.getRequestURI().getRawPath();
            if (path.endsWith("not-found")) {
                respondJson(exchange, 404, "{\"error\":\"History unavailable\"}");
            } else if (path.endsWith("missing-entry")) {
                missingEntryRequests.incrementAndGet();
                respondJson(exchange, 200, "{}");
            } else {
                respondJson(exchange, 200, "{\"prompt/id\":{\"outputs\":{\"1\":{\"text\":[\"ok\"]}}}}");
            }
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // 历史查询复用严格HTTP/JSON处理，并对缺失prompt条目给出显式错误。
            ComfyUIException notFound = Assertions.assertThrows(
                    ComfyUIException.class,
                    () -> client.getHistory("not-found").block(Duration.ofSeconds(5)));
            Assertions.assertTrue(notFound.getMessage().contains("HTTP 404"));

            ComfyUIException missing = Assertions.assertThrows(
                    ComfyUIException.class,
                    () -> client.getHistory("missing-entry").block(Duration.ofSeconds(5)));
            Assertions.assertTrue(missing.getMessage().contains("did not contain the prompt entry"));
            Assertions.assertEquals(1, missingEntryRequests.get());

            Assertions.assertNotNull(client.getHistory("prompt/id").block(Duration.ofSeconds(5)));
            Assertions.assertEquals("/history/prompt%2Fid", captured.get().rawPath());
        }
    }

    @Test
    public void testStatelessOperationsCreatedBeforeCloseFailOnNewSubscriptionWithoutNetwork() throws IOException {
        AtomicInteger serverRequests = new AtomicInteger();
        startServer(exchange -> {
            serverRequests.incrementAndGet();
            respondJson(exchange, 500, "{\"error\":\"unexpected request\"}");
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();
        ComfyUIClient client = createClient(httpClient, null);
        List<Mono<?>> operations = List.of(
                client.submit(workflow()),
                client.getJob("job-1"),
                client.cancelJob("job-1"),
                client.getHistory("prompt-1"),
                client.uploadFile("input.png", new byte[]{1}, "image/png"),
                client.getFile("output.png", "", "output"));

        client.close();
        for (Mono<?> operation : operations) {
            // close前组装、close后首次订阅属于新订阅，必须由本地生命周期门禁拒绝。
            ComfyUIException failure = Assertions.assertThrows(
                    ComfyUIException.class,
                    () -> operation.block(Duration.ofSeconds(5)));
            Assertions.assertTrue(failure.getMessage().contains("client is closed"));
        }
        Assertions.assertEquals(0, httpClient.getHttpAttempts());
        Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        Assertions.assertEquals(0, serverRequests.get());
    }

    @Test
    public void testUploadAndGetFileRemainStateless() throws IOException {
        AtomicReference<CapturedRequest> lastRequest = new AtomicReference<>();
        startServer(exchange -> {
            lastRequest.set(capture(exchange));
            if ("/upload/image".equals(exchange.getRequestURI().getRawPath())) {
                respondJson(exchange, 200, "{\"name\":\"input.png\",\"subfolder\":\"uploads\"}");
            } else {
                respond(exchange, 200, "file-data".getBytes(StandardCharsets.UTF_8), "application/octet-stream");
            }
        });
        NoWebSocketHttpClient httpClient = new NoWebSocketHttpClient();

        try (ComfyUIClient client = createClient(httpClient, null)) {
            // 文件HTTP操作执行成功且拦截器未观察到WebSocket构建，覆盖懒连接回归风险。
            UploadResult upload = client.uploadFile(
                    "input.png", "image-data".getBytes(StandardCharsets.UTF_8), "image/png")
                    .block(Duration.ofSeconds(5));
            Assertions.assertNotNull(upload);
            Assertions.assertEquals("input.png", upload.getName());
            Assertions.assertEquals("uploads", upload.getSubfolder());

            byte[] file = client.getFile("output image.png", "sub folder", "output")
                    .block(Duration.ofSeconds(5));
            Assertions.assertArrayEquals("file-data".getBytes(StandardCharsets.UTF_8), file);
            Assertions.assertEquals("/view", lastRequest.get().rawPath());
            Assertions.assertEquals("filename=output+image.png&subfolder=sub+folder&type=output", lastRequest.get().rawQuery());
            Assertions.assertEquals(0, httpClient.getWebSocketAttempts());
        }
    }

    private void startServer(HttpHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", handler);
        server.start();
    }

    private ComfyUIClient createClient(HttpClient httpClient, String apiKey) {
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "///";
        return new ComfyUIClientFactory(httpClient).create(ComfyUIClientOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .httpClient(httpClient)
                .build());
    }

    private Workflow workflow() {
        return Workflow.fromApiJson("{\"1\":{\"class_type\":\"TestNode\",\"inputs\":{}}}");
    }

    private CapturedRequest capture(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return new CapturedRequest(
                exchange.getRequestMethod(),
                exchange.getRequestURI().getRawPath(),
                exchange.getRequestURI().getRawQuery(),
                exchange.getRequestHeaders().getFirst("Authorization"),
                body);
    }

    private void respondJson(HttpExchange exchange, int status, String body) throws IOException {
        respond(exchange, status, body.getBytes(StandardCharsets.UTF_8), "application/json; charset=UTF-8");
    }

    private void respond(HttpExchange exchange, int status, byte[] body, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private record CapturedRequest(
            String method,
            String rawPath,
            String rawQuery,
            String authorization,
            String body) {
    }

    private static class NoWebSocketHttpClient extends HttpClient {

        private final HttpClient delegate = HttpClient.newHttpClient();
        private final AtomicInteger httpAttempts = new AtomicInteger();
        private final AtomicInteger webSocketAttempts = new AtomicInteger();

        public int getHttpAttempts() {
            return httpAttempts.get();
        }

        public int getWebSocketAttempts() {
            return webSocketAttempts.get();
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
            httpAttempts.incrementAndGet();
            return delegate.sendAsync(request, responseBodyHandler);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            httpAttempts.incrementAndGet();
            return delegate.sendAsync(request, responseBodyHandler, pushPromiseHandler);
        }

        @Override
        public WebSocket.Builder newWebSocketBuilder() {
            webSocketAttempts.incrementAndGet();
            throw new AssertionError("Stateless operation attempted to create a WebSocket");
        }
    }
}
