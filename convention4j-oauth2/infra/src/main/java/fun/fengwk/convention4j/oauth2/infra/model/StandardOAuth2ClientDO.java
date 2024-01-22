package fun.fengwk.convention4j.oauth2.infra.model;

import fun.fengwk.automapper.annotation.TypeHandler;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ClientStatus;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.springboot.starter.mybatis.JsonTypeHandler;
import fun.fengwk.convention4j.springboot.starter.mybatis.JsonTypeHandlers;
import fun.fengwk.convention4j.springboot.starter.persistence.ConventionDO;
import lombok.Data;

import java.util.Set;

/**
 * @author fengwk
 */
@Data
public class StandardOAuth2ClientDO extends ConventionDO<Long> {

    /**
     * 客户端标识符，全局唯一
     */
    private String clientId;

    /**
     * 客户端名称
     */
    private String name;

    /**
     * 客户端描述
     */
    private String description;

    /**
     * 客户端密钥
     */
    private String secret;

    /**
     * 客户端状态
     */
    private OAuth2ClientStatus status;

    /**
     * 客户端模式
     */
    @TypeHandler(OAuth2ModeSetTypeHandler.class)
    private Set<OAuth2Mode> modes;

    /**
     * 客户端支持的重定向地址集合
     */
    @TypeHandler(JsonTypeHandlers.StringSetTypeHandler.class)
    private Set<String> redirectUris;

    /**
     * 客户端支持的作用域单元集合
     */
    @TypeHandler(JsonTypeHandlers.StringSetTypeHandler.class)
    private Set<String> scopeUnits;

    /**
     * 授权码超时，seconds
     */
    private int authorizationCodeExpireSeconds;

    /**
     * 访问令牌超时，seconds
     */
    private int accessTokenExpireSeconds;

    /**
     * 刷新令牌超时，seconds
     */
    private int refreshTokenExpireSeconds;

    /**
     * 授权超时，seconds
     */
    private int authorizationExpireSeconds;

    /**
     * 是否允许刷新令牌
     */
    private boolean allowRefreshToken;

    /**
     * 是否允许单点登陆
     */
    private boolean allowSso;

    @UseGeneratedKeys
    @Override
    public Long getId() {
        return super.getId();
    }

    public static class OAuth2ModeSetTypeHandler extends JsonTypeHandler<Set<OAuth2Mode>> {}

}
