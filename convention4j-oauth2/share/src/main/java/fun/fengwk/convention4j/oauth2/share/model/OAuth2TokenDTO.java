package fun.fengwk.convention4j.oauth2.share.model;

import lombok.Data;

/**
 * OAuth2令牌DTO
 * @author fengwk
 */
@Data
public class OAuth2TokenDTO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 访问令牌剩余过期时间，单位/秒
     */
    private Integer expiresIn;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 作用域
     */
    private String scope;

}
