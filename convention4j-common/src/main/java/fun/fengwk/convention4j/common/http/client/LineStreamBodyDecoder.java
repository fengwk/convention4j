package fun.fengwk.convention4j.common.http.client;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 流式解码字符串行数据
 *
 * @author fengwk
 */
public class LineStreamBodyDecoder {

    private static final int CHAR_BUFFER_SIZE = 8 * 1024;
    private static final ThreadLocal<CharBuffer> CHAR_BUFFER_CACHE = ThreadLocal
        .withInitial(() -> CharBuffer.allocate(CHAR_BUFFER_SIZE));
    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    private static final int MAX_FLUSH_TIMES = 1000;

    private final String eol;
    private final StringBuilder bodyBuf = new StringBuilder();
    private final CharsetDecoder charsetDecoder;
    private ByteBuffer pendingByteBuffer;
    private boolean finished = false;

    public LineStreamBodyDecoder(Charset charset) {
        this(null, charset);
    }

    public LineStreamBodyDecoder(String eol, Charset charset) {
        if (eol != null && eol.isEmpty()) {
            throw new IllegalArgumentException("eol cannot be empty string");
        }
        this.eol = eol;
        this.charsetDecoder = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    /**
     * 追加 ByteBuffer chunk
     *
     * @param chunk chunk
     * @throws CharacterCodingException 解码异常
     */
    public synchronized void appendChunk(ByteBuffer chunk) throws CharacterCodingException {
        if (finished) {
            throw new IllegalArgumentException(String.format("%s is finished", getClass().getSimpleName()));
        }
        if (chunk == null) {
            return;
        }
        chunk = combinePendingByteBuffer(chunk);
        ByteBuffer remainingChunk = decodeChunk(chunk, false);
        if (remainingChunk != null) {
            this.pendingByteBuffer = remainingChunk;
        }
    }

    /**
     * 获取下一行数据
     *
     * @return 下一行数据，如果没有更多返回 null
     */
    public synchronized String nextLine() {
        if (finished) {
            throw new IllegalArgumentException(String.format("%s is finished", getClass().getSimpleName()));
        }
        return takeNextLine(false);
    }

    /**
     * 获取所有的剩余行
     *
     * @return 所有剩余行
     */
    public synchronized List<String> finish() throws CharacterCodingException {
        if (finished) {
            throw new IllegalArgumentException(String.format("%s is finished", getClass().getSimpleName()));
        }
        this.finished = true;
        ByteBuffer lastChunk = pendingByteBuffer;
        if (lastChunk == null) {
            lastChunk = EMPTY_BYTE_BUFFER;
        }
        decodeChunk(lastChunk, true);

        flushDecoder();
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = takeNextLine(true)) != null) {
            lines.add(line);
        }
        return lines;
    }

    private String takeNextLine(boolean endOfInput) {
        int next = endOfLine(bodyBuf, endOfInput);
        if (next != -1) {
            return take(bodyBuf, 0, next);
        }
        return null;
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
        CharBuffer flushBuf = CHAR_BUFFER_CACHE.get();
        for (int i = 0; i < MAX_FLUSH_TIMES; i++) {
            flushBuf.clear();
            CoderResult result = charsetDecoder.flush(flushBuf);
            if (result.isError()) {
                result.throwException();
            }

            flushBuf.flip();
            appendToBodyBuf(flushBuf);
            if (result.isUnderflow()) {
                // 解码器内部已清空可以退出
                break;
            }
        }
        charsetDecoder.reset();
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
        if (end == start) {
            return "";
        }
        String line = b.substring(start, end);
        b.delete(start, end);
        return line;
    }

}
