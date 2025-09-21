package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Paths;

import static fun.fengwk.convention4j.common.http.HttpHeaders.ACCEPT_ENCODING;
import static java.nio.file.StandardOpenOption.*;

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
            .bodyToString()
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
            .bodyToLines()
            .send()
            .doOnNext(System.out::println)
            .blockLast();
    }

    @Test
    public void testBodyToBase64() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.gaituya.com/img/strength/towenzi.png"))
            .GET()
            .build();

        ReactiveHttpClientUtils.buildSpec(httpRequest)
            .bodyToBase64()
            .send()
            .doOnNext(System.out::println)
            .block();
    }

//    @Test
    public void testBodyToFile() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.gaituya.com/img/strength/towenzi.png"))
            .GET()
            .build();

        ReactiveHttpClientUtils.buildSpec(httpRequest)
            .bodyToFile(Paths.get("/home/fengwk/tmp/testss.png"), CREATE, WRITE, TRUNCATE_EXISTING)
            .send()
            .doOnNext(System.out::println)
            .block();
    }

}
