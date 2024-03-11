package fun.fengwk.convention4j.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[BYTE_BUFFER_SIZE];
        int len;
        while ((len = input.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        return out.toString(charset);
    }

}
