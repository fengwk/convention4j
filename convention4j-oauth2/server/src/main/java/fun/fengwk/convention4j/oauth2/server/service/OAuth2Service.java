package fun.fengwk.convention4j.oauth2.server.service;

import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

import java.net.URI;

/**
 * OAuth2服务
 *
 * @author fengwk
 */
public interface OAuth2Service<SUBJECT, CERTIFICATE> {

    /**
     * 授权码模式、隐式模式
     *
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.AuthenticationCodeMode
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.ImplicitMode
     */
    URI authorize(AuthorizeContext<CERTIFICATE> context);

    /**
     * 授权码模式、客户端模式、密码模式、刷新令牌（clientId、clientSecret、refreshToken）
     *
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.AuthenticationCodeMode
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.ClientCredentialsMode
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.PasswordMode
     * @see fun.fengwk.convention4j.oauth2.server.service.mode.RefreshTokenService
     */
    OAuth2TokenDTO token(TokenContext context);

    /**
     * 获取授权访问主体信息
     *
     * @param accessToken 访问令牌
     * @param scope       主动指定作用域
     * @return 主体信息
     */
    SUBJECT subject(String accessToken, String scope);

    /**
     * 回收令牌
     *
     * @param accessToken 访问令牌
     */
    void revokeToken(String accessToken);

    /**
     * 获取sso存储时间
     *
     * @param clientId 客户端id
     * @return 过期时间/秒
     */
    int getSsoStoreSeconds(String clientId);

}
