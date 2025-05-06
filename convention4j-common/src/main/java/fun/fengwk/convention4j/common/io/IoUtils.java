package fun.fengwk.convention4j.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class IoUtils {

    private final static int BYTE_BUFFER_SIZE = 1024 * 8;

    private IoUtils() {}

    public static String readString(InputStream input) throws IOException {
        return readString(input, StandardCharsets.UTF_8);
    }

    public static String readString(InputStream input, Charset charset) throws IOException {
        try (ByteArrayOutputStream output = readByteArrayOutputStream(input)) {
            return output.toString(charset);
        }
    }

    public static byte[] readBytes(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = readByteArrayOutputStream(input)) {
            return output.toByteArray();
        }
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[BYTE_BUFFER_SIZE];
        int len;
        while ((len = input.read(buf)) != -1) {
            output.write(buf, 0, len);
        }
    }

    private static ByteArrayOutputStream readByteArrayOutputStream(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output;
    }

}
