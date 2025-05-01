package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
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

    private static final int CHAR_BUFFER_SIZE = 8 * 1024;
    private static final ThreadLocal<CharBuffer> CHAR_BUFFER_CACHE = ThreadLocal
            .withInitial(() -> CharBuffer.allocate(CHAR_BUFFER_SIZE));
    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);

    private final StreamBodyListener<String> lineListener;
    private final String eol;
    private final StringBuilder bodyBuf = new StringBuilder();
    private volatile CharsetDecoder charsetDecoder;
    private volatile ByteBuffer pendingByteBuffer;

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
        this.charsetDecoder = charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        lineListener.onInit(responseInfo);
    }

    @Override
    protected void onReceive0(List<ByteBuffer> chunks) throws CharacterCodingException {
        for (ByteBuffer chunk : chunks) {
            if (chunk == null) {
                continue;
            }
            chunk = combinePendingByteBuffer(chunk);
            ByteBuffer remainingChunk = decodeChunk(chunk, false);
            if (remainingChunk != null) {
                this.pendingByteBuffer = remainingChunk;
            }
            processBodyBuf(false);
        }
    }

    @Override
    public void onComplete0() throws CharacterCodingException {
        ByteBuffer lastChunk = pendingByteBuffer;
        if (lastChunk == null) {
            lastChunk = EMPTY_BYTE_BUFFER;
        }
        decodeChunk(lastChunk, true);
        flushDecoder();
        charsetDecoder.reset();
        processBodyBuf(true);
        flushRemainingBody();
        lineListener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) {
        lineListener.onError(throwable);
    }

    // 解码chunk中可用的部分, 返回剩余的chunk部分
    private ByteBuffer decodeChunk(ByteBuffer chunk, boolean endOfInput) throws CharacterCodingException {
        if (!chunk.hasRemaining() && endOfInput) {
            return doDecodeChunk(chunk, true);
        }
        while (chunk.hasRemaining()) {
            ByteBuffer remainingChunk = doDecodeChunk(chunk, endOfInput);
            if (remainingChunk != null) {
                // 还有剩余的chunk部分需要读入更多内容才能处理
                return remainingChunk;
            }
        }
        return null;
    }

    private ByteBuffer doDecodeChunk(ByteBuffer chunk, boolean endOfInput) throws CharacterCodingException {
        CharBuffer charBuffer = CHAR_BUFFER_CACHE.get().clear();
        CoderResult result = charsetDecoder.decode(chunk, charBuffer, endOfInput);
        ByteBuffer remainingChunk = handleResult(result, chunk);
        charBuffer.flip();
        appendToBodyBuf(charBuffer);
        return remainingChunk;
    }

    private void flushDecoder() throws CharacterCodingException {
        CharBuffer flushBuf = CHAR_BUFFER_CACHE.get().clear();
        for (;;) {
            CoderResult result = charsetDecoder.flush(flushBuf);
            if (result.isError()) {
                result.throwException();
            } else if (result.isOverflow()) {
                // 容量不足则需要扩展
                flushBuf = CharBuffer.allocate(flushBuf.capacity() * 2);
            } else if (result.isUnderflow()) {
                break;
            }
            flushBuf.flip();
            appendToBodyBuf(flushBuf);
        }
    }

    private void appendToBodyBuf(CharBuffer charBuf) {
        if (charBuf.hasArray()) {
            int length = charBuf.remaining();
            bodyBuf.append(charBuf.array(), charBuf.position(), length);
            charBuf.position(charBuf.position() + length);
        } else {
            while (charBuf.hasRemaining()) {
                bodyBuf.append(charBuf.get());
            }
        }
    }

    private void flushRemainingBody() {
        if (!bodyBuf.isEmpty()) {
            String line = bodyBuf.toString();
            bodyBuf.setLength(0);
            lineListener.onReceive(line);
        }
    }

    private ByteBuffer combinePendingByteBuffer(ByteBuffer chunk) {
        ByteBuffer pendingByteBuffer = this.pendingByteBuffer;
        this.pendingByteBuffer = null;

        if (pendingByteBuffer == null || !pendingByteBuffer.hasRemaining()) {
            return chunk;
        }
        if (!chunk.hasRemaining()) {
            return pendingByteBuffer;
        }

        int required = pendingByteBuffer.remaining() + chunk.remaining();
        if (required > pendingByteBuffer.capacity() || pendingByteBuffer.isReadOnly()) {
            ByteBuffer newBuffer = ByteBuffer.allocate(required);
            newBuffer.put(pendingByteBuffer);
            newBuffer.put(chunk);
            newBuffer.flip();
            return newBuffer;
        } else {
            pendingByteBuffer.compact();
            pendingByteBuffer.put(chunk);
            pendingByteBuffer.flip();
            return pendingByteBuffer;
        }
    }

    // 处理解码结果: 处理完成返回null, 有待处理chunk返回待处理的部分, 处理错误抛出异常
    private ByteBuffer handleResult(CoderResult result, ByteBuffer chunk) throws CharacterCodingException {
        if (result.isError()) {
            result.throwException();
        } else if (result.isUnderflow() && chunk.hasRemaining()) {
            return chunk;
        }
        return null;
    }

    private void processBodyBuf(boolean endOfInput) {
        int next;
        while ((next = endOfLine(bodyBuf, endOfInput)) != -1) {
            String line = take(bodyBuf, 0, next);
            lineListener.onReceive(line);
        }
    }

    // copy from LineSubscriberAdapter
    private int endOfLine(StringBuilder b, boolean endOfInput) {
        int len = b.length();
        if (eol != null) { // delimiter explicitly specified
            int i = b.indexOf(eol);
            if (i >= 0) {
                // remove the delimiter and returns the position
                // of the char after it.
                b.delete(i, i + eol.length());
                return i;
            }
        } else { // no delimiter specified, behaves as BufferedReader::readLine
            boolean crfound = false;
            for (int i = 0; i < len; i++) {
                char c = b.charAt(i);
                if (c == '\n') {
                    // '\n' or '\r\n' found.
                    // remove the delimiter and returns the position
                    // of the char after it.
                    b.delete(crfound ? i - 1 : i, i + 1);
                    return crfound ? i - 1 : i;
                } else if (crfound) {
                    // previous char was '\r', c != '\n'
                    assert i != 0;
                    // remove the delimiter and returns the position
                    // of the char after it.
                    b.delete(i - 1, i);
                    return i - 1;
                }
                crfound = c == '\r';
            }
            if (crfound && endOfInput) {
                // remove the delimiter and returns the position
                // of the char after it.
                b.delete(len - 1, len);
                return len - 1;
            }
        }
        return endOfInput && len > 0 ? len : -1;
    }

    private String take(StringBuilder b, int start, int end) {
        assert start == 0;
        String line;
        if (end == start)
            return "";
        line = b.substring(start, end);
        b.delete(start, end);
        return line;
    }

}
