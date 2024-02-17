package fun.fengwk.convention4j.oauth2.share.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 令牌类型
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum TokenType {

    BEARER("Bearer");

    private final String code;

    public static final String AUTHORIZATION = "Authorization";

    public static TokenType of(String code) {
        for (TokenType type : TokenType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public String parseAccessToken(String authorization) {
        if (authorization == null) {
            return null;
        }
        authorization = authorization.trim();
        if (!authorization.startsWith(getCode())) {
            return null;
        }
        authorization = authorization.substring(getCode().length());
        return authorization.trim();
    }

    public String buildAuthorization(String accessToken) {
        return getCode() + " " + accessToken;
    }

}
