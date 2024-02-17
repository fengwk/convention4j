package fun.fengwk.convention4j.oauth2.server.constant;

import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ScopeUtils;

import java.util.Set;

/**
 * @author fengwk
 */
public class TestConstants {

    public static final Long USER1_ID = 1L;
    public static final String USER1_USERNAME = "u1";
    public static final String USER1_PASSWORD = "111";

    public static final Long USER2_ID = 2L;
    public static final String USER2_USERNAME = "u2";
    public static final String USER2_PASSWORD = "222";

    public static final String CLIENT1_ID = "c1";
    public static final String CLIENT1_SECRET = "c1 secret";
    public static final String CLIENT1_REDIRECT_URI = "http://fengwk.fun";
    public static final String CLIENT1_SCOPE = "userInfo";
    public static final Set<String> CLIENT1_SCOPE_UNITS = OAuth2ScopeUtils.splitScope(CLIENT1_SCOPE);

    public static final String CLIENT2_ID = "c2";
    public static final String CLIENT2_SECRET = "c2 secret";
    public static final String CLIENT2_REDIRECT_URI = "http://fengwk.fun";
    public static final String CLIENT2_SCOPE = "userInfo";
    public static final Set<String> CLIENT2_SCOPE_UNITS = OAuth2ScopeUtils.splitScope(CLIENT2_SCOPE);

}
