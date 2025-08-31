package fun.fengwk.convention4j.oauth2.server.util;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.reflect.TypeToken;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.common.web.CookieUtils;
import fun.fengwk.convention4j.common.web.UriUtils;
import fun.fengwk.convention4j.oauth2.server.model.context.SsoContext;
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

    private static final String SSO_ID_COOKIE_NAME = "X-SSO-ID";

    private SsoCookieUtils() {
    }

    public static Map<String, SsoIdInfo> getSsoIdMap(HttpServletRequest request) {
        String ssoIdMapJson = CookieUtils.getCookieValue(request, SSO_ID_COOKIE_NAME);
        if (ssoIdMapJson != null) {
            ssoIdMapJson = UriUtils.decodeUriComponent(ssoIdMapJson);
            try {
                Map<String, SsoIdInfo> ssoIdMap = JsonUtils.fromJson(ssoIdMapJson, new TypeToken<>() {});
                return NullSafe.of(ssoIdMap);
            } catch (Exception ex) {
                log.error("parse ssoIdMap error, ssoIdMapJson: {}", ssoIdMapJson, ex);
            }
        }
        return Collections.emptyMap();
    }

    public static void setSsoId(HttpServletResponse response, SsoContext context, Map<String, SsoIdInfo> ssoIdMap,
                                int expireSeconds, Boolean secure) {
        String ssoId = context.getSsoId();
        if (StringUtils.isNotEmpty(ssoId)) {
            SsoIdInfo ssoIdInfo = new SsoIdInfo();
            ssoIdInfo.setId(ssoId);
            ssoIdInfo.setEt((int) (System.currentTimeMillis() / 1000L) + expireSeconds);
            ssoIdMap = new HashMap<>(ssoIdMap);
            ssoIdMap.put(context.getClientId(), ssoIdInfo);
            setSsoIdMap(response, ssoIdMap, secure);
        }
    }

    public static void deleteSsoId(HttpServletResponse response, SsoContext context, Map<String, SsoIdInfo> ssoIdMap,
                                   Boolean secure) {
        ssoIdMap = new HashMap<>(ssoIdMap);
        ssoIdMap.remove(context.getClientId());
        setSsoIdMap(response, ssoIdMap, secure);
    }

    private static void setSsoIdMap(HttpServletResponse response, Map<String, SsoIdInfo> ssoIdMap, Boolean secure) {
        String ssoIdMapJson = JsonUtils.toJson(ssoIdMap);
        ssoIdMapJson = UriUtils.encodeUriComponent(ssoIdMapJson);
        int now = (int) (System.currentTimeMillis() / 1000L);
        int maxEt = now;
        for (SsoIdInfo ssoIdInfo : ssoIdMap.values()) {
            maxEt = Math.max(maxEt, ssoIdInfo.getEt());
        }
        int expireSeconds = maxEt - now;
        CookieUtils.setCookie(response, SSO_ID_COOKIE_NAME, ssoIdMapJson, expireSeconds, secure);
    }

}
