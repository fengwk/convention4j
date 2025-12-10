package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;
import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author fengwk
 */
public class HttpClientUtilsTest {

    //    @Test
    public void testAsyncWithSSE() throws IOException, ExecutionException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("https://api.deepseek.com/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + System.getenv("DEEPSEEK_API_KEY"))
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\n" +
                    "  \"model\": \"deepseek-chat\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"Hello!\"}\n" +
                    "  ],\n" +
                    "  \"stream\": true\n" +
                    "}", StandardCharsets.UTF_8))
            .build();
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsyncWithSSEListener(
            req, new SSEListener() {
                @Override
                public void onInit(HttpResponse.ResponseInfo responseInfo) {
                    System.out.println("[SSE] init");
                }

                @Override
                public void onReceive(String line) {
                    System.out.println("[SSE] line: " + line);
                }

                @Override
                public void onReceiveEvent(SSEEvent sseEvent) {
                    System.out.println("[SSE] sseEvent: " + sseEvent);
                }

                @Override
                public void onComplete() {
                    System.out.println("[SSE] complete");
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("[SSE] error" + throwable);
                }
            });
        AsyncHttpSendResult result = future.get();
        Assertions.assertTrue(result.is2xx());
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testSendWithGzip() throws IOException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build();
        try (HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequest)) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is2xx());
            Assertions.assertFalse(sendResult.hasError());
        }
    }

    @Test
    public void testSendAsyncWithGzip() throws IOException, ExecutionException, InterruptedException {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET();

        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsync(httpRequestBuilder.build(),
            new StreamBodyListener<>() {

                @Override
                public void onInit(HttpResponse.ResponseInfo responseInfo) {
                    System.out.println("init");
                }

                @Override
                public void onReceive(List<ByteBuffer> chunk) {
                    String value = readString(chunk, StandardCharsets.UTF_8);
                    System.out.println(value);
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }

                @Override
                public void onError(Throwable throwable) {
                    Assertions.assertNull(throwable);
                }

            }, true);

        AsyncHttpSendResult result = future.get();
        String completedStr = result.getBodyCollector().parseBodyString();
        System.out.println(completedStr);

        try (HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder.build())) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is2xx());
            Assertions.assertFalse(sendResult.hasError());
            Assertions.assertEquals(sendResult.parseBodyString(), completedStr);
        }
    }

    @Test
    public void testSendAsync() throws IOException, ExecutionException, InterruptedException {
        System.out.println("req https://www.baidu.com");
        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET()
            .build());

        System.out.println("req https://www.baidu.com with gzip");
        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build());

        System.out.println("req https://www.runoob.com");
        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .GET()
            .build());

        System.out.println("req https://www.runoob.com with gzip");
        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build());

        // System.out.println("req https://www.zdic.net");
        // doTestSendAsync(HttpRequest.newBuilder()
        //     .uri(URI.create("https://www.zdic.net"))
        //     .GET()
        //     .build());
        //
        // System.out.println("req https://www.zdic.net with gzip");
        // doTestSendAsync(HttpRequest.newBuilder()
        //     .uri(URI.create("https://www.zdic.net"))
        //     .header(ACCEPT_ENCODING, "gzip")
        //     .GET()
        //     .build());
    }

    private void doTestSendAsync(HttpRequest httpRequest)
        throws InterruptedException, ExecutionException, IOException {
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsync(httpRequest,
            new StreamBodyListener<>() {

                private volatile Charset charset;

                @Override
                public void onInit(HttpResponse.ResponseInfo responseInfo) {
                    String contentType = responseInfo.headers().firstValue(CONTENT_TYPE).orElse(null);
                    this.charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
                }

                @Override
                public void onReceive(List<ByteBuffer> chunk) {
                    String str = readString(chunk, charset);
                    System.out.println("=========================");
                    System.out.println(str);
                }

                @Override
                public void onError(Throwable throwable) {
                    Assertions.assertNull(throwable);
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }

            }, true);

        AsyncHttpSendResult sendResult = future.get();
        Assertions.assertFalse(sendResult.hasError());
        String str1 = sendResult.getBodyCollector().parseBodyString();
        System.out.println(str1);

        try (HttpSendResult sendResult2 = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequest)) {
            Assertions.assertTrue(sendResult2.is2xx());
            Assertions.assertFalse(sendResult2.hasError());
            Assertions.assertEquals(str1, sendResult2.parseBodyString());
        }
    }

    @Test
    public void testAsyncWithLine() throws IOException, ExecutionException, InterruptedException {
        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET()
            .build());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .GET()
            .build());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build());

        // doTestAsyncWithLine(HttpRequest.newBuilder()
        //     .uri(URI.create("https://www.zdic.net"))
        //     .GET()
        //     .build());
        //
        // doTestAsyncWithLine(HttpRequest.newBuilder()
        //     .uri(URI.create("https://www.zdic.net"))
        //     .header(ACCEPT_ENCODING, "gzip")
        //     .GET()
        //     .build());
    }

    private void doTestAsyncWithLine(HttpRequest httpRequest)
        throws InterruptedException, ExecutionException, IOException {
        List<String> lines = new ArrayList<>();
        CompletableFuture<AsyncHttpSendResult> future = HttpClientUtils.sendAsyncWithLineListener(httpRequest,
            new StreamBodyListener<>() {

                @Override
                public void onInit(HttpResponse.ResponseInfo responseInfo) {
                }

                @Override
                public void onReceive(String line) {
                    lines.add(line);
                }

                @Override
                public void onError(Throwable throwable) {
                    Assertions.assertNull(throwable);
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }

            }, true);

        AsyncHttpSendResult sendResult = future.get();
        Assertions.assertFalse(sendResult.hasError());

        String ss = sendResult.getBodyCollector().parseBodyString();
        List<String> lines2 = new ArrayList<>();
        Scanner scanner = new Scanner(new StringReader(ss));
        while (scanner.hasNextLine()) {
            lines2.add(scanner.nextLine());
        }
        scanner.close();

        diffLines(lines, lines2);
        Assertions.assertEquals(lines, lines2);

        try (HttpSendResult sendResult2 = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequest)) {
            Assertions.assertTrue(sendResult2.is2xx());
            Assertions.assertFalse(sendResult2.hasError());
            Assertions.assertEquals(ss, sendResult2.parseBodyString());
        }
    }

    private void diffLines(List<String> lines1, List<String> lines2) {
        int i = 0;
        while (i < lines1.size() || i < lines2.size()) {
            String s1 = lines1.get(i);
            String s2 = lines2.get(i);
            if (!Objects.equals(s1, s2)) {
                System.out.println("===================================");
                System.out.println(s1);
                System.out.println("===================================");
                System.out.println(s2);
                System.out.println("===================================");
                break;
            }
            i++;
        }
    }

    private String readString(List<ByteBuffer> byteBufferList, Charset charset) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (ByteBuffer byteBuffer : byteBufferList) {
                while (byteBuffer.hasRemaining()) {
                    outputStream.write(byteBuffer.get() & 0xFF);
                }
            }
            return outputStream.toString(charset);
        } catch (IOException ignore) {
            return null;
        }
    }

}
