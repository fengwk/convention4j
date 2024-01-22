package fun.fengwk.convention4j.oauth2.infra.model;

import fun.fengwk.automapper.annotation.ExcludeField;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import fun.fengwk.convention4j.springboot.starter.persistence.ConventionDO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
@Data
public class OAuth2TokenDO extends ConventionDO<Long> {

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

    @ExcludeField
    @Override
    public LocalDateTime getCreateTime() {
        return super.getCreateTime();
    }

    @ExcludeField
    @Override
    public LocalDateTime getUpdateTime() {
        return super.getUpdateTime();
    }

}
