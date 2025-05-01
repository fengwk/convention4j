package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import fun.fengwk.convention4j.common.io.IoUtils;
import fun.fengwk.convention4j.common.util.ListUtils;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class HttpSendResult implements AutoCloseable {

    private int statusCode;
    private InputStream body;
    private Map<String, List<String>> headers;
    private Throwable error;

    public boolean is2xx() {
        return HttpUtils.is2xx(statusCode);
    }

    public boolean is3xx() {
        return HttpUtils.is3xx(statusCode);
    }

    public boolean is4xx() {
        return HttpUtils.is4xx(statusCode);
    }

    public boolean is5xx() {
        return HttpUtils.is5xx(statusCode);
    }

    public boolean hasError() {
        return error != null;
    }

    public List<String> getHeaders(String name) {
        if (name == null) {
            return Collections.emptyList();
        }

        if (headers == null) {
            return Collections.emptyList();
        }

        List<String> list = headers.get(name);
        if (list != null) {
            return list;
        }

        for (String key : headers.keySet()) {
            if (name.equalsIgnoreCase(key)) {
                return headers.get(key);
            }
        }

        return Collections.emptyList();
    }

    public String getFirstHeader(String name) {
        List<String> headers = getHeaders(name);
        return ListUtils.tryGetFirst(headers);
    }

    public String parseBodyString() throws IOException {
        if (body == null) {
            return null;
        }
        return IoUtils.readString(body, charset());
    }

    public byte[] parseBodyBytes() throws IOException {
        if (body == null) {
            return null;
        }
        return IoUtils.readBytes(body);
    }

    /**
     * 计算响应的charset
     */
    private Charset charset() {
        for (String contentType : getHeaders("Content-Type")) {
            Charset charset = HttpUtils.parseContentTypeCharset(contentType);
            if (charset != null) {
                return charset;
            }
        }
        return StandardCharsets.UTF_8;
    }

    @Override
    public void close() throws IOException {
        if (body != null) {
            body.close();
        }
    }

}
