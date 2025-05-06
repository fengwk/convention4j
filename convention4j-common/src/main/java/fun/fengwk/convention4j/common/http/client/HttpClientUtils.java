package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

/**
 * @author fengwk
 */
@Slf4j
public class HttpClientUtils {

    private static final String LOCATION = "Location";

    private HttpClientUtils() {}

    /**
     * 发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequest {@link HttpRequest}
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpClient httpClient, HttpRequest httpRequest) {
        return doSend(httpClient, httpRequest);
    }

    /**
     * 发送http请求
     *
     * @param httpClient {@link HttpClient}
     * @param httpRequestBuilder {@link HttpRequest.Builder}
     * @param redirectCount 支持重定向的次数
     * @return {@link HttpSendResult}
     */
    public static HttpSendResult send(HttpClient httpClient, HttpRequest.Builder httpRequestBuilder, int redirectCount) {
        HttpRequest.Builder copied = httpRequestBuilder.copy();
        HttpRequest httpRequest = copied.build();
        // 首次请求
        HttpSendResult sendResult = doSend(httpClient, httpRequest);

        String location;
        while (redirectCount > 0 && needRedirect(sendResult.getStatusCode())
            && (location = sendResult.getFirstHeader(LOCATION)) != null) {
            // 解析Location，如果错误将终止
            try {
                URI uri = URI.create(location);
                httpRequest = copied.uri(uri).build();
            } catch (IllegalArgumentException ex) {
                IllegalArgumentException locationEx = new IllegalArgumentException(String.format("invalid location '%s'", location), ex);
                sendResult.setError(locationEx);
                return sendResult;
            }

            // 发送新的重定向请求
            HttpSendResult redirectSendResult = doSend(httpClient, httpRequest);

            // 关闭旧的结果
            try {
                sendResult.close();
            } catch (IOException ex) {
                log.error("close http send result error", ex);
            }

            // 使用新的结果代替
            sendResult = redirectSendResult;

            // 减少重定向计数
            redirectCount--;
        }

        // 返回最终的结果
        return sendResult;
    }

    private static HttpSendResult doSend(HttpClient httpClient, HttpRequest httpRequest) {
        HttpSendResult result = new HttpSendResult();
        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            result.setStatusCode(httpResponse.statusCode());
            HttpHeaders headers = httpResponse.headers();
            if (headers != null) {
                result.setHeaders(Collections.unmodifiableMap(headers.map()));
            } else {
                result.setHeaders(Collections.emptyMap());
            }
            try {
                InputStream body = httpResponse.body();
                if (body != null && gzip(headers)) {
                    body = new GZIPInputStream(body);
                }
                result.setBody(body);
            } catch (Throwable err) {
                log.error("http client parse body error", err);
                result.setError(err);
            }
        } catch (IOException ex) {
            log.error("http client send failed", ex);
            result.setError(ex);
        } catch (InterruptedException ex) {
            log.warn("http client send interrupted", ex);
            result.setError(ex);
            Thread.currentThread().interrupt();
        } catch (Throwable err) {
            log.error("http client send error", err);
            result.setError(err);
        }
        return result;
    }

    private static boolean gzip(HttpHeaders headers) {
        if (headers == null) {
            return false;
        }
        for (String contentEncoding : headers.allValues("Content-Encoding")) {
            if (HttpUtils.gzip(contentEncoding)) {
                return true;
            }
        }
        return false;
    }

    private static boolean needRedirect(int statusCode) {
        return statusCode == HttpStatus.MOVED_PERMANENTLY.getStatus()
            || statusCode == HttpStatus.FOUND.getStatus()
            || statusCode == HttpStatus.SEE_OTHER.getStatus()
            || statusCode == HttpStatus.TEMPORARY_REDIRECT.getStatus()
            || statusCode == HttpStatus.PERMANENT_REDIRECT.getStatus();
    }

}
