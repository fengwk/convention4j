package fun.fengwk.convention4j.common.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class CookieUtils {

    private static final String DEFAULT_PATH = "/";

    private CookieUtils() {
    }

    /**
     * 通过名称获取首个匹配的Cookie
     *
     * @param cookies cookies
     * @param name    cookie name
     * @return 首个匹配的Cookie
     */
    public static Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 通过名称获取所有匹配的Cookie
     *
     * @param cookies cookies
     * @param name    cookie name
     * @return 所有匹配的Cookie
     */
    public static List<Cookie> getCookies(Cookie[] cookies, String name) {
        List<Cookie> cookieList = new ArrayList<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), name)) {
                    cookieList.add(cookie);
                }
            }
        }
        return cookieList;
    }

    /**
     * 通过名称获取首个匹配的Cookie
     *
     * @param request request
     * @param name    cookie name
     * @return 首个匹配的Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        return getCookie(request.getCookies(), name);
    }

    /**
     * 通过名称获取所有匹配的Cookie
     *
     * @param request request
     * @param name    cookie name
     * @return 所有匹配的Cookie
     */
    public static List<Cookie> getCookies(HttpServletRequest request, String name) {
        return getCookies(request.getCookies(), name);
    }

    /**
     * 通过名称获取首个匹配的Cookie值
     *
     * @param cookies cookies
     * @param name    cookie name
     * @return 首个匹配的Cookie值
     */
    public static String getCookieValue(Cookie[] cookies, String name) {
        Cookie cookie = getCookie(cookies, name);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * 通过名称获取首个匹配的Cookie值
     *
     * @param request request
     * @param name    cookie name
     * @return 首个匹配的Cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        return getCookieValue(request.getCookies(), name);
    }

    /**
     * 删除指定名称的cookie
     *
     * @param response response
     * @param name     cookie name
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        deleteCookie(response, name, DEFAULT_PATH);
    }

    /**
     * 删除指定名称的cookie
     *
     * @param response response
     * @param name     cookie name
     * @param path     cookie路径
     */
    public static void deleteCookie(HttpServletResponse response, String name, String path) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 设置Cookie，如果存在同名的Cookie则覆盖
     *
     * @param response  response
     * @param setCookie 要设置的cookie
     */
    public static void setCookie(HttpServletResponse response, Cookie setCookie) {
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
    public static void setCookie(HttpServletResponse response, String name, String value,
                                 Integer maxAge, boolean secure) {
        setCookie(response, name, value, maxAge, true, secure, DEFAULT_PATH);
    }

    /**
     * 设置Cookie
     *
     * @param response response
     * @param name     cookie name
     * @param value    cookie value
     * @param maxAge   设置过期时间，单位秒，如果设置为null则为当前浏览器会话
     * @param httpOnly 是否httpOnly
     * @param secure   仅通过https传输
     * @param path     cookie路径
     */
    public static void setCookie(HttpServletResponse response, String name, String value,
                                 Integer maxAge, boolean httpOnly, boolean secure, String path) {
        Cookie cookie = new Cookie(name, value);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path); // 必须设置path，否则默认取的是请求地址的path
        setCookie(response, cookie);
    }

}
