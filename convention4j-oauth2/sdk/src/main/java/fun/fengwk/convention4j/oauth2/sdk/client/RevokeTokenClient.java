package fun.fengwk.convention4j.oauth2.sdk.client;

import fun.fengwk.convention4j.api.result.Result;

/**
 * OAuth2客户端
 *
 * @author fengwk
 */
public interface RevokeTokenClient {

    /**
     * 回收令牌请求
     */
    Result<Void> revokeToken(String accessToken);

}
