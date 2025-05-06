package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

/**
 * @author fengwk
 */
public class HttpClientUtilsTest {

    @Test
    public void test() throws IOException {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://baidu.com"))
            .GET();
        try (HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder.build())) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is3xx());
            Assertions.assertFalse(sendResult.hasError());
        }
        try (HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequestBuilder, 1)) {
            System.out.println(sendResult);
            System.out.println(sendResult.parseBodyString());
            Assertions.assertTrue(sendResult.is2xx());
            Assertions.assertFalse(sendResult.hasError());
        }
    }

}
