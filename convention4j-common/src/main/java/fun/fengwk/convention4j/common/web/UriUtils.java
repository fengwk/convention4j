package fun.fengwk.convention4j.common.web;

import fun.fengwk.convention4j.common.lang.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author fengwk
 */
public class UriUtils {

    private static final int MAX_DECODE_COUNT = 10;

    private UriUtils() {}

    public static String encodeUriComponent(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    public static String decodeUriComponent(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    public static String fullDecodeUriComponent(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        // 直到完全解码
        int i = 0;
        String prev = str;
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        while (!Objects.equals(prev, str) && i < MAX_DECODE_COUNT) {
            prev = str;
            str = decodeUriComponent(str);
            i++;
        }
        return str;
    }

}
