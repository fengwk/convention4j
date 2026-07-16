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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fengwk
 */
public class ReactiveHttpClientUtilsTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private HttpServer server;
    private ExecutorService executor;
    private HttpClient httpClient;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        executor = Executors.newFixedThreadPool(2);
        server.setExecutor(executor);
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().set("Location", "/gzip");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.createContext("/gzip", exchange -> writeResponse(exchange, "gzip-body".getBytes(StandardCharsets.UTF_8), true));
        server.createContext("/lines", exchange -> writeResponse(
            exchange, "first\nsecond\n".getBytes(StandardCharsets.UTF_8), false));
        server.createContext("/binary", exchange -> writeResponse(exchange, new byte[] {0, 1, 2, 3}, false));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
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
    public void shouldFollowLocalRedirectAndDecodeGzip() {
        HttpRequest httpRequest = request("/redirect")
            .header(ACCEPT_ENCODING, "gzip")
            .build();

        String body = ReactiveHttpClientUtils.buildSpec(httpClient, httpRequest)
            .bodyToString()
            .send()
            .block(TIMEOUT);

        assertEquals("gzip-body", body);
    }

    @Test
    public void shouldDecodeLocalResponseLines() {
        List<String> lines = ReactiveHttpClientUtils.buildSpec(httpClient, request("/lines").build())
            .bodyToLines()
            .send()
            .collectList()
            .block(TIMEOUT);

        assertEquals(List.of("first", "second"), lines);
    }

    @Test
    public void shouldEncodeLocalBinaryResponseAsBase64() {
        String encoded = ReactiveHttpClientUtils.buildSpec(httpClient, request("/binary").build())
            .bodyToBase64()
            .send()
            .block(TIMEOUT);

        assertEquals("AAECAw==", encoded);
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(TIMEOUT)
            .GET();
    }

    private static void writeResponse(HttpExchange exchange, byte[] body, boolean gzip) throws IOException {
        byte[] responseBody = gzip ? gzip(body) : body;
        exchange.getResponseHeaders().set("Content-Type", "text/plain;charset=utf-8");
        if (gzip) {
            exchange.getResponseHeaders().set("Content-Encoding", "gzip");
        }
        exchange.sendResponseHeaders(200, responseBody.length);
        try (exchange; var output = exchange.getResponseBody()) {
            output.write(responseBody);
        }
    }

    private static byte[] gzip(byte[] body) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(output)) {
            gzip.write(body);
            gzip.finish();
            return output.toByteArray();
        }
    }
}
