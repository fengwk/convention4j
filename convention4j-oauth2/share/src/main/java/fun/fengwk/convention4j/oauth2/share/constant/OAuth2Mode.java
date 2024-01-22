package fun.fengwk.convention4j.oauth2.share.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth2模式
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum OAuth2Mode {

    AUTHORIZATION_CODE("authorization_code", ResponseType.CODE, GrantType.AUTHORIZATION_CODE),
    IMPLICIT("implicit", ResponseType.TOKEN, null),
    PASSWORD("password", null, GrantType.PASSWORD),
    CLIENT_CREDENTIALS("client_credentials", null, GrantType.CLIENT_CREDENTIALS);

    private final String code;
    private final ResponseType responseType;
    private final GrantType grantType;

    public static OAuth2Mode of(String code) {
        for (OAuth2Mode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        return null;
    }

}
