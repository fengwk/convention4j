package fun.fengwk.convention4j.oauth2.core.service.mode;

import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.core.model.AuthenticationCode;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.core.model.context.AuthenticationCodeTokenContext;
import fun.fengwk.convention4j.oauth2.core.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.core.model.context.SsoContext;
import fun.fengwk.convention4j.oauth2.core.repo.AuthenticationCodeRepository;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class AuthenticationCodeMode<SUBJECT, CERTIFICATE>
    implements OAuth2AuthorizeService<CERTIFICATE>, OAuth2TokenService<AuthenticationCodeTokenContext> {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final BaseOAuth2AuthorizeService<SUBJECT, CERTIFICATE> authorizeDelegate;
    private final BaseOAuth2TokenService<SUBJECT, CERTIFICATE, AuthenticationCodeTokenContext> tokenDelegate;

    public AuthenticationCodeMode(OAuth2ClientManager clientManager,
                                  OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                                  OAuth2TokenRepository oauth2TokenRepository,
                                  AuthenticationCodeRepository authenticationCodeRepository) {
        this.authenticationCodeRepository = authenticationCodeRepository;
        OAuth2Mode mode = OAuth2Mode.AUTHORIZATION_CODE;
        this.authorizeDelegate = new BaseOAuth2AuthorizeService<>(
            clientManager, subjectManager, oauth2TokenRepository) {
            @Override
            protected ResponseType getResponseType() {
                return mode.getResponseType();
            }

            @Override
            protected URI generateAuthorizeUri(AuthorizeContext<CERTIFICATE> context,
                                               OAuth2Client client,
                                               UriComponentsBuilder redirectUriBuilder,
                                               String subjectId) {
                String ssoId = getSsoId(context);
                boolean ssoAuthenticate = false;
                if (context instanceof SsoContext ssoContext) {
                    ssoAuthenticate = ssoContext.isSsoAuthenticate();
                }
                String code = generateAuthenticationCode(getResponseType(), client, context.getRedirectUri(),
                    context.getScope(), subjectId, ssoId, ssoAuthenticate);
                return buildAuthorizeUri(redirectUriBuilder, code, context.getState());
            }

            private URI buildAuthorizeUri(UriComponentsBuilder redirectUriBuilder, String code, String state) {
                return redirectUriBuilder
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .build().toUri();
            }
        };
        this.tokenDelegate = new BaseOAuth2TokenService<>(
            clientManager, subjectManager, oauth2TokenRepository) {
            @Override
            protected GrantType getGrantType() {
                return mode.getGrantType();
            }

            @Override
            protected OAuth2Token generateOAuth2Token(AuthenticationCodeTokenContext context, OAuth2Client client) {
                AuthenticationCode authenticationCode = checkAndGetAuthenticationCode(context.getCode());
                if (authenticationCode.isSsoAuthenticate()) {
                    // 单点登陆的情况，直接返回单点登陆对应的令牌
                    return oauth2TokenRepository.getBySsoId(authenticationCode.getSsoId());
                } else {
                    // 走正常的登陆流程
                    checkRedirectUri(context.getRedirectUri(), authenticationCode);
                    return generateToken(authenticationCode.getSubjectId(), authenticationCode.getScope(),
                        client, authenticationCode.getSsoId());
                }
            }
        };
    }


    @Override
    public String supportResponseType() {
        return authorizeDelegate.supportResponseType();
    }

    @Override
    public URI authorize(AuthorizeContext<CERTIFICATE> context) {
        return authorizeDelegate.authorize(context);
    }

    @Override
    public String supportGrantType() {
        return tokenDelegate.supportGrantType();
    }

    @Override
    public OAuth2TokenDTO token(AuthenticationCodeTokenContext context) {
        return tokenDelegate.token(context);
    }

    private String generateAuthenticationCode(
        ResponseType responseType, OAuth2Client client, String redirectUri,
        String scope, String subjectId, String ssoId, boolean ssoAuthenticate) {
        // 生成授权码
        AuthenticationCode authenticationCode = AuthenticationCode.generate(
            subjectId,
            responseType,
            client.getClientId(),
            redirectUri,
            scope,
            ssoId,
            ssoAuthenticate);
        if (!authenticationCodeRepository.add(authenticationCode, client.getAuthorizationCodeExpireSeconds())) {
            log.error("add authenticationCode failed, authenticationCode: {}, authorizationCodeExpireSeconds: {}",
                authenticationCode, client.getAuthorizationCodeExpireSeconds());
            throw OAuth2ErrorCodes.GENERATE_AUTHENTICATION_CODE_FAILED.asThrowable();
        }

        // 返回授权码
        return authenticationCode.getCode();
    }

    private AuthenticationCode checkAndGetAuthenticationCode(String code) {
        AuthenticationCode authenticationCode = authenticationCodeRepository.get(code);
        if (authenticationCode == null) {
            log.warn("invalid authentication code, code: {}", code);
            throw OAuth2ErrorCodes.INVALID_AUTHENTICATION_CODE.asThrowable();
        } else {

        }
        return authenticationCode;
    }

    private void checkRedirectUri(String redirectUri, AuthenticationCode authenticationCode) {
        UriComponents curUri = UriComponentsBuilder.fromUriString(redirectUri).build();
        UriComponents storeUri = UriComponentsBuilder.fromUriString(authenticationCode.getRedirectUri()).build();
        if (!Objects.equals(curUri.getScheme(), storeUri.getScheme())
            || !Objects.equals(curUri.getUserInfo(), storeUri.getUserInfo())
            || !Objects.equals(curUri.getHost(), storeUri.getHost())
            || !Objects.equals(curUri.getPort(), storeUri.getPort())
            || !Objects.equals(curUri.getPath(), storeUri.getPath())
            || !Objects.equals(curUri.getFragment(), storeUri.getFragment())) {
            log.warn("redirectUri not match, currentRedirectUri: {}, storeRedirectUri: {}",
                redirectUri, authenticationCode.getRedirectUri());
            throw OAuth2ErrorCodes.INVALID_REDIRECT_URI.asThrowable();
        }
    }

}
