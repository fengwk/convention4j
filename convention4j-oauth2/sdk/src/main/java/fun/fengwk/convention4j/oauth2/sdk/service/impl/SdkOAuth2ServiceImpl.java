package fun.fengwk.convention4j.oauth2.sdk.service.impl;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.sdk.client.RevokeTokenClient;
import fun.fengwk.convention4j.oauth2.sdk.client.TokenClient;
import fun.fengwk.convention4j.oauth2.sdk.client.model.TokenParams;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2TokenSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.service.SdkOAuth2Service;
import fun.fengwk.convention4j.oauth2.sdk.utils.OAuth2UriUtils;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class SdkOAuth2ServiceImpl<CERTIFICATE> implements SdkOAuth2Service {

    private final OAuth2TokenSessionManager oauth2TokenSessionManager;
    private final OAuth2StateSessionManager oauth2StateSessionManager;
    private final TokenClient<CERTIFICATE> tokenClient;
    private final RevokeTokenClient revokeTokenClient;
    private final OAuth2SdkProperties oauth2SdkProperties;

    @Override
    public OAuth2TokenDTO authenticationCode(String code, String redirectUri, String state) {
        if (oauth2StateSessionManager.verifyState(state)) {
            oauth2StateSessionManager.invalidState(state);
        } else {
            throw OAuth2UriUtils.injectOAuth2UriIfNecessary(
                OAuth2ErrorCodes.INVALID_STATE, oauth2StateSessionManager, oauth2SdkProperties);
        }
        TokenParams<CERTIFICATE> params = new TokenParams<>();
        params.setGrantType(GrantType.AUTHORIZATION_CODE.getCode());
        params.setCode(code);
        params.setRedirectUri(redirectUri);
        Result<OAuth2TokenDTO> result = tokenClient.token(params);
        if (!result.isSuccess()) {
            log.error("get oauth2 token failed, message: {}", result.getMessage());
            throw result.getErrorCode().asThrowable();
        }
        OAuth2TokenDTO tokenDTO = result.getData();
        oauth2TokenSessionManager.add(tokenDTO);
        return tokenDTO;
    }

    @Override
    public void revokeToken(String accessToken) {
        Result<Void> result = revokeTokenClient.revokeToken(accessToken);
        if (!result.isSuccess()) {
            log.error("revoke oauth2 token failed, message: {}", result.getMessage());
        }
    }

}
