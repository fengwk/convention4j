package fun.fengwk.convention4j.common.http.client;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReactiveHttpClientUtilsLocalTest {

    @Test
    void shouldConsumeRealJdkHttpResponseBody() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        server.setExecutor(executor);
        server.createContext("/body", exchange -> {
            byte[] body = "real-response".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain;charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (exchange; var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/body"))
                .GET()
                .build();

            String body = ReactiveHttpClientUtils.buildSpec(HttpClient.newHttpClient(), request)
                .bodyToString()
                .send()
                .block(Duration.ofSeconds(5));

            assertEquals("real-response", body);
        } finally {
            server.stop(0);
            executor.shutdownNow();
        }
    }

    @Test
    void shouldPreserveLineOrderAndFinishTrailingLine() {
        ReactiveHttpSendResult<Flux<ByteBuffer>> result = resultWithBody(
            "first\nsec".getBytes(StandardCharsets.UTF_8),
            "ond".getBytes(StandardCharsets.UTF_8));

        // Completion must flush the final unterminated line after prior chunks in source order.
        List<String> lines = ReactiveHttpClientUtils.bodyToLines(result).collectList().block();

        assertEquals(List.of("first", "second"), lines);
    }

    @Test
    void shouldFinishTrailingSseEventAfterOrderedChunks() {
        ReactiveHttpSendResult<Flux<ByteBuffer>> result = resultWithBody(
            "data: first\n\ndata: sec".getBytes(StandardCharsets.UTF_8),
            "ond".getBytes(StandardCharsets.UTF_8));

        // The SSE decoder must see the final line and dispatch its trailing event at source completion.
        List<SSEEvent> events = ReactiveHttpClientUtils.bodyToSSE(result).collectList().block();

        assertEquals(List.of("first", "second"), events.stream().map(SSEEvent::getData).toList());
    }

    private static ReactiveHttpSendResult<Flux<ByteBuffer>> resultWithBody(byte[]... chunks) {
        ReactiveHttpSendResult<Flux<ByteBuffer>> result = new ReactiveHttpSendResult<>();
        result.setBody(Flux.fromArray(chunks).map(ByteBuffer::wrap));
        return result;
    }
}
