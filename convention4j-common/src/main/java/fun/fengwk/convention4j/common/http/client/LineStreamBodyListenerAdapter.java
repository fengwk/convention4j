package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_TYPE;

/**
 * 允许一行行读取 body 数据的{@link StreamBodyListener}适配器
 *
 * @author fengwk
 */
@Slf4j
public class LineStreamBodyListenerAdapter extends AbstractStreamBodyListener<List<ByteBuffer>> {

    private final StreamBodyListener<String> lineListener;
    private final String eol;
    private volatile LineStreamBodyDecoder lineDecoder;

    public LineStreamBodyListenerAdapter(StreamBodyListener<String> lineListener) {
        this(lineListener, null);
    }

    public LineStreamBodyListenerAdapter(StreamBodyListener<String> lineListener, String eol) {
        this.lineListener = Objects.requireNonNull(lineListener);
        this.eol = eol;
    }

    @Override
    protected void onInit0(HttpResponse.ResponseInfo responseInfo) {
        String contentType = responseInfo.headers().firstValue(CONTENT_TYPE).orElse(null);
        Charset charset = HttpUtils.parseContentTypeCharset(contentType, HttpUtils.DEFAULT_CHARSET);
        this.lineDecoder = new LineStreamBodyDecoder(eol, charset);
        lineListener.onInit(responseInfo);
    }

    @Override
    protected void onReceive0(List<ByteBuffer> chunks) throws CharacterCodingException {
        for (ByteBuffer chunk : chunks) {
            lineDecoder.appendChunk(chunk);
            String line;
            while ((line = lineDecoder.nextLine()) != null) {
                lineListener.onReceive(line);
            }
        }
    }

    @Override
    public void onComplete0() throws CharacterCodingException {
        for (String line : lineDecoder.finish()) {
            lineListener.onReceive(line);
        }
        lineListener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) {
        lineListener.onError(throwable);
    }

}
