package fun.fengwk.convention4j.oauth2.sdk.config;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class OAuth2ContextConfig {

    /**
     * OAuthContext是否支持刷新令牌
     */
    private boolean refreshable = true;

    /**
     * 上下文请求时默认携带的scope
     */
    private String defaultSubjectScope = "";

}
