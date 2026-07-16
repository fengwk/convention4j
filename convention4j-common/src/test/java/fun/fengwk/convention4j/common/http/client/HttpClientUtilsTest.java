package fun.fengwk.convention4j.common.http.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fengwk
 */
public class HttpClientUtilsTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String PLAIN_BODY = "plain-body";
    private static final String GZIP_BODY = "gzip-body";
    private static final String LINES_BODY = "first\nsecond\n";

    private HttpServer server;
    private ExecutorService executor;
    private HttpClient httpClient;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        executor = Executors.newFixedThreadPool(2);
        server.setExecutor(executor);
        server.createContext("/plain", exchange -> writeResponse(
            exchange, PLAIN_BODY.getBytes(StandardCharsets.UTF_8), false, "text/plain;charset=utf-8"));
        server.createContext("/gzip", exchange -> writeResponse(
            exchange, GZIP_BODY.getBytes(StandardCharsets.UTF_8), true, "text/plain;charset=utf-8"));
        server.createContext("/lines", exchange -> writeResponse(
            exchange, LINES_BODY.getBytes(StandardCharsets.UTF_8), false, "text/plain;charset=utf-8"));
        server.createContext("/gzip-lines", exchange -> writeResponse(
            exchange, LINES_BODY.getBytes(StandardCharsets.UTF_8), true, "text/plain;charset=utf-8"));
        server.createContext("/sse", exchange -> writeResponse(
            exchange, "event: notice\ndata: first\n\ndata: second\n\n".getBytes(StandardCharsets.UTF_8), false,
            "text/event-stream;charset=utf-8"));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldReceiveLocalSseEvents() throws Exception {
        List<String> lines = new CopyOnWriteArrayList<>();
        List<SSEEvent> events = new CopyOnWriteArrayList<>();
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsyncWithSSEListener(
            httpClient, request("/sse").build(), new SSEListener() {
                @Override
                public void onReceive(String line) {
                    lines.add(line);
                }

                @Override
                public void onReceiveEvent(SSEEvent event) {
                    events.add(event);
                }
            }, true);

        AsyncHttpSendResult result = future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertTrue(result.is2xx());
        assertFalse(result.hasError());
        assertEquals(List.of("event: notice", "data: first", "", "data: second", ""), lines);
        assertEquals(List.of("first", "second"), events.stream().map(SSEEvent::getData).toList());
    }

    @Test
    public void shouldSendAndDecodeLocalGzipSynchronously() throws IOException {
        try (HttpSendResult result = HttpClientUtils.send(httpClient, request("/gzip")
                .header(ACCEPT_ENCODING, "gzip")
                .build())) {
            assertTrue(result.is2xx());
            assertFalse(result.hasError());
            assertEquals(GZIP_BODY, result.parseBodyString());
        }
    }

    @Test
    public void shouldCollectLocalGzipBodyAsynchronously() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsync(
            httpClient, request("/gzip").header(ACCEPT_ENCODING, "gzip").build(),
            new StreamBodyListener<>() {
                @Override
                public void onReceive(List<ByteBuffer> chunk) {
                    received.add(new String(BufferUtils.toBytes(chunk), StandardCharsets.UTF_8));
                }
            }, true);

        AsyncHttpSendResult result = future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertTrue(result.is2xx());
        assertFalse(result.hasError());
        assertEquals(GZIP_BODY, result.getBodyCollector().parseBodyString());
        assertEquals(GZIP_BODY, String.join("", received));
    }

    @Test
    public void shouldMatchSynchronousAndAsynchronousLocalBodies() throws Exception {
        assertAsyncBodyMatchesSync("/plain", PLAIN_BODY, false);
        assertAsyncBodyMatchesSync("/gzip", GZIP_BODY, true);
    }

    @Test
    public void shouldDeliverLocalPlainAndGzipLines() throws Exception {
        assertLocalLines("/lines", false);
        assertLocalLines("/gzip-lines", true);
    }

    private void assertAsyncBodyMatchesSync(String path, String expectedBody, boolean gzip) throws Exception {
        HttpRequest.Builder builder = request(path);
        if (gzip) {
            builder.header(ACCEPT_ENCODING, "gzip");
        }
        HttpRequest request = builder.build();
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsync(httpClient, request,
            new StreamBodyListener<>() {
            }, true);

        AsyncHttpSendResult asyncResult = future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertFalse(asyncResult.hasError());
        assertEquals(expectedBody, asyncResult.getBodyCollector().parseBodyString());
        try (HttpSendResult syncResult = HttpClientUtils.send(httpClient, request)) {
            assertTrue(syncResult.is2xx());
            assertFalse(syncResult.hasError());
            assertEquals(expectedBody, syncResult.parseBodyString());
        }
    }

    private void assertLocalLines(String path, boolean gzip) throws Exception {
        List<String> lines = new CopyOnWriteArrayList<>();
        HttpRequest.Builder builder = request(path);
        if (gzip) {
            builder.header(ACCEPT_ENCODING, "gzip");
        }
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsyncWithLineListener(
            httpClient, builder.build(), new StreamBodyListener<>() {
                @Override
                public void onReceive(String line) {
                    lines.add(line);
                }
            }, true);

        AsyncHttpSendResult result = future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertFalse(result.hasError());
        assertEquals(LINES_BODY, result.getBodyCollector().parseBodyString());
        assertEquals(List.of("first", "second"), lines);
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(TIMEOUT)
            .GET();
    }

    private static void writeResponse(HttpExchange exchange, byte[] body, boolean gzip, String contentType)
            throws IOException {
        byte[] responseBody = gzip ? gzip(body) : body;
        exchange.getResponseHeaders().set("Content-Type", contentType);
        if (gzip) {
            exchange.getResponseHeaders().set("Content-Encoding", "gzip");
        }
        exchange.sendResponseHeaders(200, responseBody.length);
        try (exchange; var output = exchange.getResponseBody()) {
            output.write(responseBody);
        }
    }

    private static byte[] gzip(byte[] body) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
                gzip.write(body);
                gzip.finish();
            }
            return outputStream.toByteArray();
        }
    }

}
