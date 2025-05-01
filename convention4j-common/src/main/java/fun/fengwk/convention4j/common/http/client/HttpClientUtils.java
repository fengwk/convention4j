package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
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

    private HttpClientUtils() {}

    public static HttpSendResult send(HttpClient httpClient, HttpRequest httpRequest) {
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

}
