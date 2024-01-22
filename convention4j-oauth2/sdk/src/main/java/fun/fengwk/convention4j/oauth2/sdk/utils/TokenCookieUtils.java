package fun.fengwk.convention4j.oauth2.sdk.utils;

import fun.fengwk.convention4j.common.web.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author fengwk
 */
public class TokenCookieUtils {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    private TokenCookieUtils() {
    }

    public static String getAccessToken(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public static void setAccessToken(HttpServletRequest request, HttpServletResponse response,
                                      String accessToken, int expireSeconds) {
        CookieUtils.setCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, expireSeconds);
    }

    public static void deleteAccessToken(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME);
    }

}
