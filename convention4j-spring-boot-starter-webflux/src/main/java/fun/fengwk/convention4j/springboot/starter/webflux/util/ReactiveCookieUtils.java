package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.ListUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class ReactiveCookieUtils {

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
        if (httpCookies != null) {
            HttpCookie httpCookie = ListUtils.tryGetFirst(httpCookies);
            return NullSafe.map(httpCookie, HttpCookie::getValue);
        }
        return null;
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
        if (httpCookies != null) {
            HttpCookie httpCookie = ListUtils.tryGetFirst(httpCookies);
            return NullSafe.map(httpCookie, HttpCookie::getValue);
        }
        return null;
    }

    /**
     * 删除指定名称的cookie
     *
     * @param request  request
     * @param response response
     * @param name     cookie name
     */
    public static void deleteCookie(ServerHttpRequest request, ServerHttpResponse response, String name) {
        List<HttpCookie> httpCookies = request.getCookies().get(name);
        if (httpCookies != null) {
            for (HttpCookie httpCookie : httpCookies) {
                if (Objects.equals(httpCookie.getName(), name)) {
                    ResponseCookie respCookie = ResponseCookie
                        .from(httpCookie.getName(), httpCookie.getValue())
                        .maxAge(0)
                        .build();
                    response.addCookie(respCookie);
                }
            }
        }
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param request   request
     * @param response  response
     * @param setCookie 要设置的cookie
     */
    public static void setCookie(ServerHttpRequest request, ServerHttpResponse response, ResponseCookie setCookie) {
        deleteCookie(request, response, setCookie.getName());
        response.addCookie(setCookie);
    }

    /**
     * 默认的cookie设置方法，将cookie域名与当前请求保持一致，路径设置为/，且httponly，在大多数情况下适用
     *
     * @param request  request
     * @param response response
     * @param name     cookie name
     * @param value    cookie value
     * @param maxAge   设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     */
    public static void setCookie(ServerHttpRequest request, ServerHttpResponse response,
                                 String name, String value, Integer maxAge) {
        ResponseCookie.ResponseCookieBuilder respCookieBuilder = ResponseCookie
            .from(name, value)
            .httpOnly(true)
            .path("/"); // 必须设置path，否则默认取的是请求地址的path
        String host = request.getHeaders().getFirst(HttpHeaders.HOST);
        if (StringUtils.isNotEmpty(host)) {
            respCookieBuilder.domain(host);
        }
        if (maxAge != null) {
            respCookieBuilder.maxAge(maxAge);
        }
        setCookie(request, response, respCookieBuilder.build());
    }

    /**
     * 删除指定名称的cookie
     *
     * @param request         request
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     */
    public static void deleteCookie(ServerRequest request, ServerResponse.BodyBuilder responseBuilder, String name) {
        List<HttpCookie> httpCookies = request.cookies().get(name);
        if (httpCookies != null) {
            httpCookies.stream()
                .map(httpCookie -> ResponseCookie.from(httpCookie.getName(), httpCookie.getValue())
                    .maxAge(0).build())
                .forEach(responseBuilder::cookie);
        }
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param request         request
     * @param responseBuilder responseBuilder
     * @param setCookie       要设置的cookie
     */
    public static void setCookie(ServerRequest request, ServerResponse.BodyBuilder responseBuilder, ResponseCookie setCookie) {
        deleteCookie(request, responseBuilder, setCookie.getName());
        responseBuilder.cookie(setCookie);
    }

    /**
     * 默认的cookie设置方法，将cookie域名与当前请求保持一致，路径设置为/，且httponly，在大多数情况下适用
     *
     * @param request         request
     * @param responseBuilder responseBuilder
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAge          设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     */
    public static void setCookie(ServerRequest request, ServerResponse.BodyBuilder responseBuilder,
                                 String name, String value, Integer maxAge) {
        ResponseCookie.ResponseCookieBuilder respCookieBuilder = ResponseCookie
            .from(name, value)
            .httpOnly(true)
            .path("/"); // 必须设置path，否则默认取的是请求地址的path
        String host = request.headers().firstHeader(HttpHeaders.HOST);
        if (StringUtils.isNotEmpty(host)) {
            respCookieBuilder.domain(host);
        }
        if (maxAge != null) {
            respCookieBuilder.maxAge(maxAge);
        }
        setCookie(request, responseBuilder, respCookieBuilder.build());
    }

}
