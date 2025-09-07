package fun.fengwk.convention4j.oauth2.server.util;

import fun.fengwk.convention4j.common.lang.StringUtils;

/**
 * @author fengwk
 */
public class OAuth2Utils {

    private OAuth2Utils() {}

    public static String normalizeEmptyPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "/";
        }
        return path;
    }

}
