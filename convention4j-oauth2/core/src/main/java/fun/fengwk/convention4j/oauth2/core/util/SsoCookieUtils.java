package fun.fengwk.convention4j.oauth2.core.util;

import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.web.CookieUtils;
import fun.fengwk.convention4j.common.web.UriUtils;
import fun.fengwk.convention4j.oauth2.core.model.context.SsoContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
public class SsoCookieUtils {

    private static final String SSO_ID_COOKIE_NAME = "SSO-ID";

    private SsoCookieUtils() {
    }

    public static Map<String, String> getSsoIdMap(HttpServletRequest request) {
        String ssoIdMapJson = CookieUtils.getCookieValue(request, SSO_ID_COOKIE_NAME);
        if (ssoIdMapJson != null) {
            ssoIdMapJson = UriUtils.decodeUriComponent(ssoIdMapJson);
            try {
                return GsonUtils.fromJson(ssoIdMapJson, new TypeToken<Map<String, String>>() {}.getType());
            } catch (Exception ex) {
                log.error("parse ssoIdMap error, ssoIdMapJson: {}", ssoIdMapJson, ex);
            }
        }
        return Collections.emptyMap();
    }

    public static void setSsoId(HttpServletRequest request, HttpServletResponse response,
                                   SsoContext context, Map<String, String> ssoIdMap, int expireSeconds) {
        ssoIdMap = new HashMap<>(ssoIdMap);
        ssoIdMap.put(context.getClientId(), context.getSsoId());
        setSsoIdMap(request, response, ssoIdMap, expireSeconds);
    }

    public static void deleteSsoId(HttpServletRequest request, HttpServletResponse response,
                                   SsoContext context, Map<String, String> ssoIdMap, int expireSeconds) {
        ssoIdMap = new HashMap<>(ssoIdMap);
        ssoIdMap.remove(context.getClientId());
        setSsoIdMap(request, response, ssoIdMap, expireSeconds);
    }

    private static void setSsoIdMap(HttpServletRequest request, HttpServletResponse response,
                                    Map<String, String> ssoIdMap, int expireSeconds) {
        String ssoIdMapJson = GsonUtils.toJson(ssoIdMap);
        ssoIdMapJson = UriUtils.encodeUriComponent(ssoIdMapJson);
        CookieUtils.setCookie(request, response, SSO_ID_COOKIE_NAME, ssoIdMapJson, expireSeconds);
    }

}
