package fun.fengwk.convention4j.oauth2.server.model;

import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import lombok.Data;

import java.util.Set;

/**
 * @author fengwk
 */
@Data
public class Client implements OAuth2Client {

    /**
     * 客户端标识符，全局唯一
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String secret;

    /**
     * 客户端模式
     */
    private Set<OAuth2Mode> modes;

    /**
     * 客户端支持的重定向地址集合
     */
    private Set<String> redirectUris;

    /**
     * 客户端支持的作用域单元集合
     */
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
    private int authorizeExpireSeconds;

    /**
     * 是否允许刷新令牌
     */
    private boolean allowRefreshToken;

    /**
     * 是否允许单点登陆
     */
    private boolean allowSso;

}
