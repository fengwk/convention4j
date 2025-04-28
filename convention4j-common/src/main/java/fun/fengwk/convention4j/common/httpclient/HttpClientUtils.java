package fun.fengwk.convention4j.common.httpclient;

import fun.fengwk.convention4j.common.io.IoUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
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
                String respBody = HttpClientUtils.parseBodyString(httpResponse);
                result.setBody(respBody);
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

    public static boolean is2xx(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public static String parseBodyString(HttpResponse<InputStream> httpResponse) throws IOException {
        HttpHeaders headers = httpResponse.headers();
        InputStream input = httpResponse.body();
        try {
            if (gzip(headers)) {
                input = new GZIPInputStream(input);
            }
            return IoUtils.readString(input, charset(headers));
        } finally {
            input.close();
        }
    }

    private static boolean gzip(HttpHeaders headers) {
        for (String contentEncoding : headers.allValues("Content-Encoding")) {
            if (contentEncoding != null && contentEncoding.contains("gzip")) {
                return true;
            }
        }
        return false;
    }

    private static Charset charset(HttpHeaders headers) {
        for (String contentType : headers.allValues("Content-Type")) {
            Charset charset = null;
            try {
                charset = parseContentTypeCharset(contentType);
            } catch (UnsupportedCharsetException ignore) {
                log.warn("not support charset, contentType: {}", contentType);
            }
            if (charset != null) {
                return charset;
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static Charset parseContentTypeCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        int i = contentType.indexOf(";");
        if (i == -1) {
            return null;
        }

        String charset = contentType.substring(i + 1);
        charset = charset.replaceAll(" ", "");
        if (charset.startsWith("charset=")) {
            charset = charset.substring("charset=".length());
        }
        return Charset.forName(charset);
    }

}
