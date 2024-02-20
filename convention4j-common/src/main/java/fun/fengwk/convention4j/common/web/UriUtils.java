package fun.fengwk.convention4j.common.web;

import fun.fengwk.convention4j.common.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author fengwk
 */
public class UriUtils {

    private static final int MAX_DECODE_COUNT = 5;

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
        // 直到完全解码
        int i = 0;
        String prev = str;
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        while (!Objects.equals(prev, str) && i < MAX_DECODE_COUNT) {
            prev = str;
            str = URLDecoder.decode(str, StandardCharsets.UTF_8);
            i++;
        }
        return str;
    }

}
