package fun.fengwk.convention4j.common.web;

import fun.fengwk.convention4j.common.lang.StringUtils;
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

    private static final String HOST = "Host";

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
     * @param request  request
     * @param response response
     * @param name     cookie name
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), name)) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
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
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, Cookie setCookie) {
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
    public static void setCookie(HttpServletRequest request, HttpServletResponse response,
                                 String name, String value, Integer maxAge) {
        String host = request.getHeader(HOST);
        Cookie cookie = new Cookie(name, value);
        if (StringUtils.isNotEmpty(host)) {
            cookie.setDomain(host);
        }
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 必须设置path，否则默认取的是请求地址的path
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        setCookie(request, response, cookie);
    }

}
