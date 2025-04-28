//package fun.fengwk.convention4j.common.httpclient;
//
//import org.junit.jupiter.api.Test;
//
//import java.net.URI;
//import java.net.http.HttpRequest;
//import java.time.Duration;
//
///**
// * @author fengwk
// */
//public class HttpClientUtilsTest {
//
//    @Test
//    public void test() {
//        HttpRequest httpRequest = HttpRequest.newBuilder()
//            .uri(URI.create("https://baidu.com"))
//            .GET()
//            .timeout(Duration.ofMillis(1000 * 3L))
//            .build();
//        HttpSendResult sendResult = HttpClientUtils.send(HttpClientFactory.getDefaultHttpClient(), httpRequest);
//        System.out.println(sendResult.getStatusCode());
//        System.out.println(sendResult.getHeaders());
//        System.out.println(sendResult.getBody());
//        System.out.println(sendResult.getError());
//    }
//
//}
