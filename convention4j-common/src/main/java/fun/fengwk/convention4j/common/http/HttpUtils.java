package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
@Slf4j
public class HttpUtils {

    private static final String CONTENT_TYPE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String KEY_CHARSET = "charset";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static boolean is2xx(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public static boolean is3xx(int statusCode) {
        return statusCode >= 300 && statusCode < 400;
    }

    public static boolean is4xx(int statusCode) {
        return statusCode >= 400 && statusCode < 500;
    }

    public static boolean is5xx(int statusCode) {
        return statusCode >= 500 && statusCode < 600;
    }

    /**
     * 检查是否包含gzip标识
     *
     * @param contentEncoding Content-Encoding请求头
     * @return 是否包含gzip标识
     */
    public static boolean gzip(String contentEncoding) {
        return contentEncoding != null && contentEncoding.contains("gzip");
    }

    /**
     * 解析Content-Type中的charset类型
     *
     * @param contentType Content-Type请求头
     * @param defaultCharset 默认的字符集
     * @return 如果包含合法的Charset则返回对应Charset，否则返回null
     */
    public static Charset parseContentTypeCharset(String contentType, Charset defaultCharset) {
        Charset charset = parseContentTypeCharset(contentType);
        return charset == null ? defaultCharset : charset;
    }

    /**
     * 解析Content-Type中的charset类型
     *
     * @param contentType Content-Type请求头
     * @return 如果包含合法的Charset则返回对应Charset，否则返回null
     */
    public static Charset parseContentTypeCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        List<String> parts = splitContentType(contentType);
        for (String part : parts) {
            Pair<String, String> keyValue = parseKeyValue(part);
            if (keyValue != null && KEY_CHARSET.equalsIgnoreCase(keyValue.getKey())) {
                return parseCharset(keyValue.getValue());
            }
        }

        return null;
    }

    private static Charset parseCharset(String charset) {
        if (StringUtils.isBlank(charset)) {
            return null;
        }

        try {
            return Charset.forName(charset);
        } catch (IllegalCharsetNameException ex) {
            log.warn("illegal charset, charset: {}", charset);
        } catch (UnsupportedCharsetException ex) {
            log.warn("not support charset, charset: {}", charset);
        }
        return null;
    }

    /**
     * 分割Content-Type信息
     *
     * @param contentType Content-Type信息
     * @return 分割后的列表，剔除了空文本
     */
    public static List<String> splitContentType(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            return Collections.emptyList();
        }

        String[] parts = contentType.split(CONTENT_TYPE_SEPARATOR);
        List<String> splitList = new ArrayList<>();
        for (String part : parts) {
            if (StringUtils.isNotBlank(part)) {
                splitList.add(part.trim());
            }
        }
        return splitList;
    }

    /**
     * 解析key=value键值对字符串
     *
     * @param keyValue key=value键值对字符串
     * @return 解析后的Pair
     */
    public static Pair<String, String> parseKeyValue(String keyValue) {
        if (StringUtils.isBlank(keyValue)) {
            return null;
        }

        int idx = keyValue.indexOf(KEY_VALUE_SEPARATOR);
        if (idx == -1) {
            return null;
        }

        String key = keyValue.substring(0, idx);
        String value = keyValue.substring(idx + 1);
        return Pair.of(key.trim(), value.trim());
    }

    /**
     * 符合 RFC 3986 的 url 编码
     *
     * @param text decoded text
     * @return encoded text
     */
    public static String encodeUrlComponent(String text, Charset charset) {
        return URLEncoder.encode(text, charset)
            .replaceAll("\\+", "%20");
    }

    /**
     * 符合 RFC 3986 的 url 解码
     *
     * @param text encoded text
     * @return decoded text
     */
    public static String decodeUrlComponent(String text, Charset charset) {
        return URLDecoder.decode(text, charset);
    }

}
