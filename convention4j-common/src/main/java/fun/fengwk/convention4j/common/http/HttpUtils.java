package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author fengwk
 */
@Slf4j
public class HttpUtils {

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
     * @return 如果包含合法的Charset则返回对应Charset，否则返回null
     */
    public static Charset parseContentTypeCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        int i = contentType.indexOf(";");
        if (i == -1) {
            return null;
        }

        String charset = contentType.substring(i + 1);
        charset = charset.replaceAll(" ", "");
        if (charset.startsWith("charset=")) {
            charset = charset.substring("charset=".length());
        }

        return parseCharset(charset);
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

}
