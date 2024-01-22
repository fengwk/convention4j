package fun.fengwk.convention4j.oauth2.sdk.utils;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.PrototypeErrorCode;
import fun.fengwk.convention4j.api.code.ThrowableErrorCode;
import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * @author fengwk
 */
public class OAuth2UriUtils {

    private static final String ERROR_KEY_OAUTH2_URI = "oauth2Uri";

    private OAuth2UriUtils() {
    }

    /**
     * 将oauth2Uri注入错误码中构建错误码异常
     */
    public static ThrowableErrorCode injectOAuth2UriIfNecessary(
        PrototypeErrorCode errorCode,
        OAuth2StateSessionManager oauth2StateSessionManager,
        OAuth2SdkProperties oauth2SdkProperties) {
        URI oauth2Uri = null;
        if (StringUtils.isNotBlank(oauth2SdkProperties.getOauth2Uri())) {
            Set<OAuth2Mode> modes = oauth2SdkProperties.getModes();
            if (modes.contains(OAuth2Mode.AUTHORIZATION_CODE)) {
                oauth2Uri = generateOAuth2Uri(
                    oauth2SdkProperties.getOauth2Uri(),
                    OAuth2Mode.AUTHORIZATION_CODE.getResponseType(),
                    oauth2SdkProperties.getClientId(),
                    oauth2SdkProperties.getRedirectUri(),
                    oauth2SdkProperties.getScope(),
                    oauth2StateSessionManager.generateState());
            } else if (modes.contains(OAuth2Mode.IMPLICIT)) {
                oauth2Uri = generateOAuth2Uri(
                    oauth2SdkProperties.getOauth2Uri(),
                    OAuth2Mode.IMPLICIT.getResponseType(),
                    oauth2SdkProperties.getClientId(),
                    oauth2SdkProperties.getRedirectUri(),
                    oauth2SdkProperties.getScope(),
                    oauth2StateSessionManager.generateState());
            }
        }
        ErrorCode finalErrorCode = errorCode;
        if (oauth2Uri != null) {
            Map<String, Object> errorContext = MapUtils.newMap(ERROR_KEY_OAUTH2_URI, oauth2Uri);
            finalErrorCode = errorCode.resolve(errorContext);
        }
        throw finalErrorCode.asThrowable();
    }

    public static URI generateOAuth2Uri(String oauth2Uri,
                                        ResponseType responseType,
                                        String clientId,
                                        String redirectUri,
                                        String scope,
                                        String state) {
        if (responseType == null) {
            return null;
        }
        return UriComponentsBuilder
            .fromUriString(oauth2Uri)
            .queryParam("responseType", responseType.getCode())
            .queryParam("clientId", clientId)
            .queryParam("redirectUri", redirectUri)
            .queryParam("scope", scope)
            .queryParam("state", state)
            .build().toUri();
    }

}
