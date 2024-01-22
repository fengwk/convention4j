package fun.fengwk.convention4j.oauth2.sdk.config;

import fun.fengwk.convention4j.api.code.PrototypeErrorCode;
import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.utils.OAuth2UriUtils;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.oauth2.sdk")
public class OAuth2SdkProperties {

    /**
     * oauth2 base uri
     * @see OAuth2UriUtils#injectOAuth2UriIfNecessary(PrototypeErrorCode, OAuth2StateSessionManager, OAuth2SdkProperties)
     */
    private String oauth2Uri;

    /**
     * oath2的重定向地址，默认为占位符
     */
    private String redirectUri = "{redirectUri}";

    /**
     * oath2的作用域，默认为占位符
     */
    private String scope = "{scope}";

    /**
     * oauth2 base api uri
     */
    private String oauth2ApiBaseUri;

    /**
     * 当前应用使用的oauth2模式，如果包含{@link OAuth2Mode#AUTHORIZATION_CODE}且配置了{@link #getOauth2Uri()}
     * 将会在访问令牌失效时返回oauth2授权地址
     */
    private Set<String> modes = Collections.emptySet();

    /**
     * 当前应用的clientId
     */
    private String clientId;

    /**
     * 当前应用的clientSecret
     */
    private String clientSecret;

    /**
     * httpClient配置
     */
    private HttpClientConfig httpClient = new HttpClientConfig();

    /**
     * oauth2上下文配置
     */
    private OAuth2ContextConfig oauth2Context = new OAuth2ContextConfig();

    /**
     * state session配置
     */
    private StateSessionConfig stateSession = new StateSessionConfig();

    /**
     * token session配置
     */
    private TokenSessionConfig tokenSession = new TokenSessionConfig();

    public Set<OAuth2Mode> getModes() {
        return NullSafe.of(modes).stream().map(OAuth2Mode::of).collect(Collectors.toSet());
    }

}
