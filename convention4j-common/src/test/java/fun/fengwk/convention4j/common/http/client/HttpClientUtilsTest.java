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
import java.util.concurrent.Flow;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;
import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author fengwk
 */
public class HttpClientUtilsTest {

//    @Test
    public void testSend() throws IOException {
//        System.setProperty("javax.net.debug", "ssl,handshake");
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://baidu.com/"))
            .GET();
        try (HttpSendResult sendResult = HttpClientUtils.send(httpRequestBuilder.build())) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is3xx());
            Assertions.assertFalse(sendResult.hasError());
        }
        try (HttpSendResult sendResult = HttpClientUtils.send(httpRequestBuilder, 1)) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is2xx());
            Assertions.assertFalse(sendResult.hasError());
        }
    }

//    @Test
    public void testAsyncWithSSE() throws IOException, ExecutionException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("https://api.deepseek.com/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
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
        CompletableFuture<HttpSendResult> future = HttpClientUtils.sendAsyncWithSSEListener(
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
                public void onReceiveComment(String comment) {
                    System.out.println("[SSE] comment: " + comment);
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
        HttpSendResult result = future.get();
        Assertions.assertTrue(result.is2xx());
        Assertions.assertFalse(result.hasError());
        String body = result.parseBodyString();
        System.out.println("SSE Body=====================================");
        System.out.println(body);
    }

    @Test
    public void testSendWithGzip() throws IOException {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET();
        try (HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder.build())) {
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

        CompletableFuture<HttpResponse<List<ByteBuffer>>> future = HttpClientFactory.getDefaultHttpClient()
            .sendAsync(httpRequestBuilder.build(), HttpClientUtils.fromSubscriber(new Flow.Subscriber<>() {

                private volatile Flow.Subscription subscription;

                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    this.subscription = subscription;
                    subscription.request(1);
                }

                @Override
                public void onNext(List<ByteBuffer> items) {
                    System.out.println("====================================================");
                    String value = readString(items, StandardCharsets.UTF_8);
                    System.out.println(value);
                    subscription.request(1);
                }

                @Override
                public void onError(Throwable throwable) {
                    Assertions.assertNull(throwable);
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }
            }));

        HttpResponse<List<ByteBuffer>> response = future.get();
        List<ByteBuffer> byteBufferList = response.body();
        String contentType = response.headers().firstValue(CONTENT_TYPE).orElse(null);
        String completedStr = readString(byteBufferList, HttpUtils.parseContentTypeCharset(contentType, StandardCharsets.UTF_8));
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
        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET());

        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());

        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .GET());

        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());

        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.zdic.net"))
            .GET());

        doTestSendAsync(HttpRequest.newBuilder()
            .uri(URI.create("https://www.zdic.net"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());
    }

    private void doTestSendAsync(HttpRequest.Builder httpRequestBuilder)
            throws InterruptedException, ExecutionException, IOException {
        CompletableFuture<HttpSendResult> future = HttpClientUtils.sendAsync(httpRequestBuilder.build(), new StreamBodyListener<>() {

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

        });

        HttpSendResult sendResult = future.get();
        Assertions.assertFalse(sendResult.hasError());
        System.out.println(sendResult.parseBodyString());

        try (HttpSendResult sendResult2 = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder.build())) {
            Assertions.assertTrue(sendResult2.is2xx());
            Assertions.assertFalse(sendResult2.hasError());
            Assertions.assertEquals(sendResult.parseBodyString(), sendResult2.parseBodyString());
        }
    }

    @Test
    public void testAsyncWithLine() throws IOException, ExecutionException, InterruptedException {
        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.runoob.com"))
            .GET());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.zdic.net"))
            .GET());

        doTestAsyncWithLine(HttpRequest.newBuilder()
            .uri(URI.create("https://www.zdic.net"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET());
    }

    private void doTestAsyncWithLine(HttpRequest.Builder httpRequestBuilder)
            throws InterruptedException, ExecutionException, IOException {
        List<String> lines = new ArrayList<>();
        CompletableFuture<HttpSendResult> future = HttpClientUtils.sendAsyncWithLineListener(httpRequestBuilder.build(),
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

            });

        HttpSendResult sendResult = future.get();
        Assertions.assertFalse(sendResult.hasError());

        String ss = sendResult.parseBodyString();
        List<String> lines2 = new ArrayList<>();
        Scanner scanner = new Scanner(new StringReader(ss));
        while (scanner.hasNextLine()) {
            lines2.add(scanner.nextLine());
        }
        scanner.close();

        diffLines(lines, lines2);
        Assertions.assertEquals(lines, lines2);

        try (HttpSendResult sendResult2 = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder.build())) {
            Assertions.assertTrue(sendResult2.is2xx());
            Assertions.assertFalse(sendResult2.hasError());
            Assertions.assertEquals(sendResult.parseBodyString(), sendResult2.parseBodyString());
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
