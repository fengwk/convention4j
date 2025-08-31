package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.common.util.ListUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

/**
 * @author fengwk
 */
public class ReactiveCookieUtils {

    private static final String DEFAULT_PATH = "/";

    private ReactiveCookieUtils() {
    }

    /**
     * 通过名称获取首个匹配的Cookie值
     *
     * @param request request
     * @param name    cookie name
     * @return 首个匹配的Cookie值
     */
    public static String getCookieValue(ServerHttpRequest request, String name) {
        List<HttpCookie> httpCookies = request.getCookies().get(name);
        return getCookieValue(httpCookies);
    }

    /**
     * 通过名称获取首个匹配的Cookie值
     *
     * @param request request
     * @param name    cookie name
     * @return 首个匹配的Cookie值
     */
    public static String getCookieValue(ServerRequest request, String name) {
        List<HttpCookie> httpCookies = request.cookies().get(name);
        return getCookieValue(httpCookies);
    }

    /**
     * 删除指定名称的cookie
     *
     * @param response response
     * @param name     cookie name
     */
    public static void deleteCookie(ServerHttpResponse response, String name) {
        deleteCookie(response, name, DEFAULT_PATH);
    }

    /**
     * 删除指定名称的cookie
     *
     * @param response response
     * @param name     cookie name
     * @param path     cookie路径
     */
    public static void deleteCookie(ServerHttpResponse response, String name, String path) {
        ResponseCookie respCookie = ResponseCookie
            .from(name, "")
            .path(path)
            .maxAge(0)
            .build();
        response.addCookie(respCookie);
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param response  response
     * @param setCookie 要设置的cookie
     */
    public static void setCookie(ServerHttpResponse response, ResponseCookie setCookie) {
        response.addCookie(setCookie);
    }

    /**
     * 默认的cookie设置方法，将cookie路径设置为/，且httponly，在大多数情况下适用
     *
     * @param response response
     * @param name     cookie name
     * @param value    cookie value
     * @param maxAge   设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     */
    public static void setCookie(ServerHttpResponse response,
                                 String name, String value, Integer maxAge) {
        setCookie(response, name, value, maxAge, true, DEFAULT_PATH);
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param response response
     * @param name     cookie name
     * @param value    cookie value
     * @param maxAge   设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     * @param httpOnly 是否设置httpOnly
     * @param path     cookie路径
     */
    public static void setCookie(ServerHttpResponse response,
                                 String name, String value, Integer maxAge, boolean httpOnly, String path) {
        ResponseCookie.ResponseCookieBuilder respCookieBuilder = ResponseCookie
            .from(name, value)
            .httpOnly(httpOnly)
            .path(path); // 必须设置path，否则默认取的是请求地址的path
        if (maxAge != null) {
            respCookieBuilder.maxAge(maxAge);
        }
        setCookie(response, respCookieBuilder.build());
    }

    /**
     * 删除指定名称的cookie
     *
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     */
    public static void deleteCookie(ServerResponse.BodyBuilder responseBuilder, String name) {
        deleteCookie(responseBuilder, name, DEFAULT_PATH);
    }

    /**
     * 删除指定名称的cookie
     *
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     * @param path            cookie路径
     */
    public static void deleteCookie(ServerResponse.BodyBuilder responseBuilder, String name, String path) {
        responseBuilder.cookie(ResponseCookie.from(name, "")
            .path(path)
            .maxAge(0)
            .build());
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param responseBuilder responseBuilder
     * @param setCookie       要设置的cookie
     */
    public static void setCookie(ServerResponse.BodyBuilder responseBuilder, ResponseCookie setCookie) {
        responseBuilder.cookie(setCookie);
    }

    /**
     * 默认的cookie设置方法，将cookie路径设置为/，且httponly，在大多数情况下适用
     *
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAge          设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     */
    public static void setCookie(ServerResponse.BodyBuilder responseBuilder,
                                 String name, String value, Integer maxAge, boolean secure) {
        setCookie(responseBuilder, name, value, maxAge, true, secure, DEFAULT_PATH, "Lax");
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAge          设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     */
    public static void setCookie(ServerResponse.BodyBuilder responseBuilder,
                                 String name, String value, Integer maxAge, boolean secure, String sameSite) {
        setCookie(responseBuilder, name, value, maxAge, true, secure, DEFAULT_PATH, sameSite);
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAge          设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     * @param httpOnly        是否设置httpOnly
     * @param secure          仅通过https传输
     * @param path            cookie路径
     * @param sameSite        跨站请求防护
     */
    public static void setCookie(ServerResponse.BodyBuilder responseBuilder, String name, String value,
                                 Integer maxAge, boolean httpOnly, boolean secure, String path, String sameSite) {
        ResponseCookie.ResponseCookieBuilder respCookieBuilder = ResponseCookie
            .from(name, value)
            .httpOnly(httpOnly)
            .secure(secure)
            .sameSite(sameSite)
            .path(path); // 必须设置path，否则默认取的是请求地址的path
        if (maxAge != null) {
            respCookieBuilder.maxAge(maxAge);
        }
        setCookie(responseBuilder, respCookieBuilder.build());
    }

    private static String getCookieValue(List<HttpCookie> httpCookies) {
        if (httpCookies != null) {
            HttpCookie httpCookie = ListUtils.tryGetFirst(httpCookies);
            return NullSafe.map(httpCookie, HttpCookie::getValue);
        }
        return null;
    }

}
