package fun.fengwk.convention4j.common.compress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPOutputStream;

/**
 * @author fengwk
 */
public class GzipDecoderTest {

    @Test
    public void test1() throws IOException, DataFormatException {
        String orStr = "aaaaaaaaaaaaasssssssssssssdddddddddffffffff你好";
        byte[] gzipBytes = gzipEncode(orStr);
        test1Byte(gzipBytes, orStr);
    }

    @Test
    public void test2() throws IOException, DataFormatException {
        String orStr = "aaaaaaaaaaaaassssssss京津冀sssssdddddddddffffffff你好";
        NonblockingGzipEncoder encoder = new NonblockingGzipEncoder(false);
        ByteBuffer encodedBuffer = encoder.encode(ByteBuffer.wrap(orStr.getBytes(StandardCharsets.UTF_8)));
        ByteBuffer endBuffer = encoder.endOfInput();
        byte[] encodedBytes = toBytes(encodedBuffer);
        byte[] endBytes = toBytes(endBuffer);
        byte[] gzipBytes = new byte[encodedBytes.length + endBytes.length];
        System.arraycopy(encodedBytes, 0, gzipBytes, 0, encodedBytes.length);
        System.arraycopy(endBytes, 0, gzipBytes, encodedBytes.length, endBytes.length);
        test1Byte(gzipBytes, orStr);
    }

    private static void test1Byte(byte[] gzipBytes, String orStr) {
        NonblockingGzipDecoder gzipDecoder = new NonblockingGzipDecoder(5);
        ByteArrayOutputStream decodedBytesOutput = new ByteArrayOutputStream();
        for (byte b : gzipBytes) {
            ByteBuffer decodeBuf = gzipDecoder.decode(ByteBuffer.wrap(new byte[]{b}));
            if (!decodeBuf.hasRemaining()) {
                continue;
            }
            byte[] decodedBytes = toBytes(decodeBuf);
            System.out.println(new String(decodedBytes, StandardCharsets.UTF_8));
            decodedBytesOutput.write(decodedBytes, 0, decodedBytes.length);
        }
        String decodedStr = decodedBytesOutput.toString(StandardCharsets.UTF_8);
        Assertions.assertEquals(orStr, decodedStr);
    }

    private static byte[] toBytes(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        return bytes;
    }

    private static byte[] gzipEncode(String orStr) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(orStr.getBytes(StandardCharsets.UTF_8));
        gzipOutputStream.close();
        return outputStream.toByteArray();
    }

}
