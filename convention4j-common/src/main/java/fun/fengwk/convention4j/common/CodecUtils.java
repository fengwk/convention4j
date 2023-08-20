package fun.fengwk.convention4j.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fengwk
 */
public class CodecUtils {

    private CodecUtils() {}

    public static byte[] md5(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5Hex(byte[] bytes) {
        return bytes2hexStr(md5(bytes));
    }

    public static String bytes2hexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(0xff & b));
        }
        return sb.toString();
    }

}
