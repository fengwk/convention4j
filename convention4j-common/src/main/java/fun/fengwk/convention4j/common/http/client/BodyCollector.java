package fun.fengwk.convention4j.common.http.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
public class BodyCollector {

    private final List<ByteBuffer> result = Collections.synchronizedList(new ArrayList<>());
    private volatile Charset charset;

    BodyCollector() {}

    void collect(List<ByteBuffer> byteBufferList) {
        for (ByteBuffer byteBuffer : byteBufferList) {
            collect(byteBuffer);
        }
    }

    void collect(ByteBuffer byteBuffer) {
        result.add(copy(byteBuffer));
    }

    void setCharset(Charset charset) {
        this.charset = charset;
    }

    public List<ByteBuffer> get() {
        return Collections.unmodifiableList(result);
    }

    public byte[] toBytes() {
        return BufferUtils.toBytes(result);
    }

    public String parseBodyString() {
        return parseBodyString(charset == null ? StandardCharsets.UTF_8 : charset);
    }

    public String parseBodyString(Charset charset) {
        byte[] bodyBytes = toBytes();
        if (bodyBytes == null) {
            return null;
        }
        return new String(bodyBytes, charset);
    }

    private ByteBuffer copy(ByteBuffer byteBuffer) {
        ByteBuffer source = byteBuffer.duplicate();
        ByteBuffer copy = ByteBuffer.allocate(source.remaining());
        copy.put(source);
        copy.flip();
        return copy;
    }

}
