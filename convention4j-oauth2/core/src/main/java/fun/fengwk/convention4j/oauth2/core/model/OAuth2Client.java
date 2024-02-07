package fun.fengwk.convention4j.oauth2.core.model;

import fun.fengwk.convention4j.common.AntPattern;
import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ScopeUtils;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Set;

/**
 * OAuth2客户端
 *
 * @author fengwk
 */
public interface OAuth2Client {

    /**
     * 获取客户端id
     *
     * @return 客户端id
     */
    String getClientId();

    /**
     * 获取客户端密钥
     *
     * @return 客户端密钥
     */
    String getSecret();

    /**
     * 获取支持的授权模式集合
     *
     * @return 支持的授权模式集合
     */
    Set<OAuth2Mode> getModes();

    /**
     * 获取支持的重定向uri集合
     *
     * @return 支持的重定向uri集合
     */
    Set<String> getRedirectUris();

    /**
     * 获取当前客户端支持的作用域单元集合
     *
     * @return 支持的作用域单元集合
     */
    Set<String> getScopeUnits();

    /**
     * 获取授权码超时，单位/秒
     *
     * @return 授权码超时，单位/秒
     */
    int getAuthorizationCodeExpireSeconds();

    /**
     * 获取访问令牌超时，单位/秒
     *
     * @return 访问令牌超时，单位/秒
     */
    int getAccessTokenExpireSeconds();

    /**
     * 获取刷新令牌超时，单位/秒
     *
     * @return 刷新令牌超时，单位/秒
     */
    int getRefreshTokenExpireSeconds();

    /**
     * 获取授权超时，单位/秒
     *
     * @return 授权超时，单位/秒
     */
    int getAuthorizeExpireSeconds();

    /**
     * 是否允许刷新令牌
     *
     * @return true表示允许，false表示不允许
     */
    boolean isAllowRefreshToken();

    /**
     * 是否允许单点登陆
     *
     * @return true表示允许，false表示不允许
     */
    boolean isAllowSso();

    /**
     * 检查指定的客户端密钥是否正确
     *
     * @param secret 客户端密钥
     * @return 是否正确
     */
    default boolean validateSecret(String secret) {
        return Objects.equals(getSecret(), secret);
    }

    /**
     * 是否支持指定的重定向uri
     *
     * @param redirectUri 重定向uri
     * @return 是否支持
     */
    default boolean supportRedirectUri(String redirectUri) {
        if (StringUtils.isBlank(redirectUri)) {
            return true;
        }
        UriComponents uri = UriComponentsBuilder.fromUriString(redirectUri).build();
        String scheme = uri.getScheme();
        String userInfo = uri.getUserInfo();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String fragment = uri.getFragment();
        for (String supportRedirectUri : getRedirectUris()) {
            try {
                UriComponents supportUri = UriComponentsBuilder.fromUriString(supportRedirectUri).build();
                String supportScheme = supportUri.getScheme();
                String supportUserInfo = supportUri.getUserInfo();
                String supportHost = supportUri.getHost();
                int supportPort = supportUri.getPort();
                String supportPath = supportUri.getPath();
                String supportFragment = supportUri.getFragment();
                if (Objects.equals(scheme, supportScheme) && Objects.equals(userInfo, supportUserInfo)
                    && new AntPattern(supportHost).match(host)
                    && Objects.equals(port, supportPort)
                    && new AntPattern(supportPath).match(path)
                    && (supportFragment == null || new AntPattern(supportFragment).match(fragment))) {
                    return true;
                }
            } catch (IllegalArgumentException ignore) {
                if (Objects.equals(supportRedirectUri, redirectUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否支持指定的响应类型
     *
     * @param responseType 响应类型
     * @return 是否支持
     */
    default boolean supportResponseType(ResponseType responseType) {
        for (OAuth2Mode mode : NullSafe.of(getModes())) {
            if (Objects.equals(mode.getResponseType(), responseType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持指定的授权模式
     *
     * @param grantType 授权模式
     * @return 是否支持
     */
    default boolean supportGrantType(GrantType grantType) {
        if (grantType == GrantType.REFRESH_TOKEN && isAllowRefreshToken()) {
            return true;
        }
        for (OAuth2Mode mode : NullSafe.of(getModes())) {
            if (Objects.equals(mode.getGrantType(), grantType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持指定的作用域单元
     *
     * @param scopeUnit 作用域单元
     * @return 是否支持
     */
    default boolean supportScopeUnit(String scopeUnit) {
        if (StringUtils.isBlank(scopeUnit)) {
            return true;
        }
        Set<String> scopeUnits = getScopeUnits();
        String[] scopeItems = StringUtils.split(scopeUnit, ",");
        for (String scopeItem : scopeItems) {
            if (scopeUnits.contains(scopeItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持指定的作用域
     *
     * @param scope 作用域
     * @return 是否支持
     */
    default boolean supportScope(String scope) {
        Set<String> scopeUnits = OAuth2ScopeUtils.splitScope(scope);
        for (String scopeUnit : scopeUnits) {
            if (!supportScopeUnit(scopeUnit)) {
                return false;
            }
        }
        return true;
    }

}
