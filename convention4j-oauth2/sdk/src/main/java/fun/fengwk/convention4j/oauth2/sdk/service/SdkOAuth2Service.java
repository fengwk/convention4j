package fun.fengwk.convention4j.oauth2.sdk.service;

import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

/**
 * @author fengwk
 */
public interface SdkOAuth2Service {

    /**
     * 通过授权码获取OAuth2令牌
     *
     * @param code 授权码
     * @param redirectUri 重定向地址
     * @param state 登陆状态
     * @return OAuth2令牌
     * @throws fun.fengwk.convention4j.api.code.ThrowableErrorCode 如果获取失败将抛出该异常并携带具体错误码
     */
    OAuth2TokenDTO authenticationCode(String code, String redirectUri, String state);

    /**
     * 回收访问令牌
     *
     * @param accessToken 访问令牌
     */
    void revokeToken(String accessToken);

}
