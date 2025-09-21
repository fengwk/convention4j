package fun.fengwk.convention4j.common.http.client;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author fengwk
 */
class BufferUtils {

    private BufferUtils() {}

    static byte[] toBytes(List<ByteBuffer> byteBuffers) {
        int len = 0;
        for (ByteBuffer byteBuffer : byteBuffers) {
            len += byteBuffer.remaining();
        }
        byte[] bodyBytes = new byte[len];
        int offset = 0;
        for (ByteBuffer byteBuffer : byteBuffers) {
            int rem = byteBuffer.remaining();
            byteBuffer.get(bodyBytes, offset, rem);
            offset += rem;
        }
        return bodyBytes;
    }

}
