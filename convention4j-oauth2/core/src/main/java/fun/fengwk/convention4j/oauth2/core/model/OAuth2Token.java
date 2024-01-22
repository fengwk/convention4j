package fun.fengwk.convention4j.oauth2.core.model;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * OAuth2令牌
 * @author fengwk
 */
@Data
public class OAuth2Token {

    /**
     * 令牌id
     */
    private long id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 主体id
     */
    private String subjectId;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 令牌类型
     */
    private TokenType tokenType;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 单点登陆id
     */
    private String ssoId;

    /**
     * 最后一次刷新的时间
     */
    private LocalDateTime lastRefreshTime;

    /**
     * 授权的时间
     */
    private LocalDateTime authorizeTime;

    /**
     * 生成OAuth2令牌BO
     */
    public static OAuth2Token generate(long id, String clientId, String subjectId, String scope, String ssoId) {
        OAuth2Token oauth2Token = new OAuth2Token();
        oauth2Token.setId(id);
        oauth2Token.setClientId(clientId);
        oauth2Token.setSubjectId(subjectId);
        oauth2Token.setScope(scope);
        oauth2Token.setTokenType(TokenType.BEARER);
        oauth2Token.setAccessToken(generateToken());
        oauth2Token.setRefreshToken(generateToken());
        oauth2Token.setSsoId(ssoId);
        LocalDateTime now = LocalDateTime.now();
        oauth2Token.setLastRefreshTime(now);
        oauth2Token.setAuthorizeTime(now);
        return oauth2Token;
    }

    /**
     * 生成令牌
     */
    private static String generateToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return sb.toString();
    }

    /**
     * 获取访问令牌剩余有效时间，单位/秒
     */
    public Integer accessTokenExpiresIn(Integer accessTokenExpireSeconds) {
        if (accessTokenExpireSeconds == null) {
            return null;
        }
        LocalDateTime lastRefreshTime = getLastRefreshTime();
        LocalDateTime expiresTime = lastRefreshTime.plusSeconds(accessTokenExpireSeconds);
        return (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresTime);
    }

    /**
     * 获取刷新令牌剩余有效时间，单位/秒
     */
    public Integer refreshTokenExpiresIn(Integer refreshTokenExpireSeconds) {
        if (refreshTokenExpireSeconds == null) {
            return null;
        }
        LocalDateTime lastRefreshTime = getLastRefreshTime();
        LocalDateTime expiresTime = lastRefreshTime.plusSeconds(refreshTokenExpireSeconds);
        return (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresTime);
    }

    /**
     * 获取授权剩余有效时间，单位/秒
     */
    public Integer authorizationExpiresIn(Integer authorizationExpireSeconds) {
        if (authorizationExpireSeconds == null) {
            return null;
        }
        LocalDateTime authorizeTime = getAuthorizeTime();
        LocalDateTime expiresTime = authorizeTime.plusSeconds(authorizationExpireSeconds);
        return (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresTime);
    }

    /**
     * 访问令牌是否过期
     */
    public boolean accessTokenExpired(int accessTokenExpireSeconds) {
        return accessTokenExpiresIn(accessTokenExpireSeconds) <= 0;
    }

    /**
     * 刷新令牌是否过期
     */
    public boolean refreshTokenExpired(int refreshTokenExpireSeconds) {
        return refreshTokenExpiresIn(refreshTokenExpireSeconds) <= 0;
    }

    /**
     * 授权是否过期
     */
    public boolean authorizationExpired(int authorizationExpireSeconds) {
        return authorizationExpiresIn(authorizationExpireSeconds) <= 0;
    }

    /**
     * 刷新令牌
     */
    public void refresh() {
        setAccessToken(generateToken());
        setRefreshToken(generateToken());
        setLastRefreshTime(LocalDateTime.now());
    }

    /**
     * 转换为DTO
     */
    public OAuth2TokenDTO toDTO(Integer accessTokenExpireSeconds){
        OAuth2TokenDTO oauth2TokenDTO = new OAuth2TokenDTO();
        oauth2TokenDTO.setAccessToken(getAccessToken());
        oauth2TokenDTO.setTokenType(NullSafe.map(getTokenType(), TokenType::getCode));
        if (accessTokenExpireSeconds != null) {
            oauth2TokenDTO.setExpiresIn(accessTokenExpiresIn(accessTokenExpireSeconds));
        }
        oauth2TokenDTO.setRefreshToken(getRefreshToken());
        oauth2TokenDTO.setScope(getScope());
        return oauth2TokenDTO;
    }

}
