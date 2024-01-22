package fun.fengwk.convention4j.oauth2.sdk.manager;

import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

/**
 * @author fengwk
 */
public interface OAuth2TokenSessionManager {

    /**
     * 存储令牌到会话中
     */
    void add(OAuth2TokenDTO tokenDTO);

    /**
     * 通过访问令牌移除对应的实际令牌
     */
    void remove(String accessToken);

    /**
     * 通过访问令牌获取对应的令牌
     */
    OAuth2TokenDTO get(String accessToken);

}
