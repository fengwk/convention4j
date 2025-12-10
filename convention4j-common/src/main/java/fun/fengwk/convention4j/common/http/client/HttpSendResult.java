package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import fun.fengwk.convention4j.common.io.IoUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author fengwk
 */
@Slf4j
@Data
public class HttpSendResult extends AbstractHttpSendResult implements AutoCloseable {

    private URI uri;
    private InputStream body;
    private byte[] bodyBytes;

    public String tryParseBodyString() {
        try {
            return parseBodyString();
        } catch (IOException ignore) {
            return null;
        }
    }

    public String parseBodyString() throws IOException {
        return parseBodyString(charset());
    }

    public String parseBodyString(Charset charset) throws IOException {
        byte[] bodyBytes = parseBodyBytes();
        if (bodyBytes == null) {
            return null;
        }
        return new String(bodyBytes, charset);
    }

    public byte[] tryParseBodyBytes() {
        try {
            return parseBodyBytes();
        } catch (IOException ignore) {
            return null;
        }
    }

    public byte[] parseBodyBytes() throws IOException {
        if (bodyBytes == null) {
            if (body == null) {
                return null;
            }
            bodyBytes = IoUtils.readBytes(body);
        }
        return bodyBytes;
    }

    /**
     * 计算响应的charset
     */
    private Charset charset() {
        for (String contentType : getHeaders(CONTENT_TYPE)) {
            Charset charset = HttpUtils.parseContentTypeCharset(contentType);
            if (charset != null) {
                return charset;
            }
        }
        return HttpUtils.DEFAULT_CHARSET;
    }

    @Override
    public void close() {
        if (body != null) {
            try {
                body.close();
            } catch (IOException ex) {
                log.error("close body error", ex);
            }
            body = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

}
