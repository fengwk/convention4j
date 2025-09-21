package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;

/**
 * @author fengwk
 */
public class ReactiveHttpClientUtilsTest {

    @Test
    public void testSendWithGzip() {
        HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/redirect/3"))
            .header(ACCEPT_ENCODING, "gzip")
            .GET()
            .build();

        ReactiveHttpClientUtils.buildSpec(httpClient, httpRequest)
            .bodyToStringMono()
            .send()
            .doOnNext(System.out::println)
            .block();
    }

    @Test
    public void testSendWithGzip2() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET()
            .build();

        ReactiveHttpClientUtils.buildSpec(httpRequest)
            .bodyToLineFlux()
            .send()
            .doOnNext(System.out::println)
            .blockLast();
    }

}
