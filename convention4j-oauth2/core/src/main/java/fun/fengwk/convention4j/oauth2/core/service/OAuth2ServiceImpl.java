package fun.fengwk.convention4j.oauth2.core.service;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ScopeUtils;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.core.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.core.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.core.service.mode.OAuth2AuthorizeService;
import fun.fengwk.convention4j.oauth2.core.service.mode.OAuth2TokenService;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class OAuth2ServiceImpl<SUBJECT, CERTIFICATE> implements OAuth2Service<SUBJECT, CERTIFICATE> {

    private final Map<String, OAuth2AuthorizeService<CERTIFICATE>> oauth2AuthorizeServiceRegistry;
    private final Map<String, OAuth2TokenService<? extends TokenContext>> oauth2TokenServiceRegistry;
    private final OAuth2ClientManager oauth2ClientManager;
    private final OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager;
    private final OAuth2TokenRepository oauth2TokenRepository;

    public OAuth2ServiceImpl(List<OAuth2AuthorizeService<CERTIFICATE>> oauth2AuthorizeServices,
                             List<OAuth2TokenService<? extends TokenContext>> oauth2TokenServices,
                             OAuth2ClientManager oauth2ClientManager,
                             OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
                             OAuth2TokenRepository oauth2TokenRepository) {
        Objects.requireNonNull(oauth2AuthorizeServices, "oauth2AuthorizeServices can not be null");
        Objects.requireNonNull(oauth2TokenServices, "oauth2TokenServices cannot be null");
        this.oauth2AuthorizeServiceRegistry = oauth2AuthorizeServices.stream()
            .collect(Collectors.toMap(OAuth2AuthorizeService::supportResponseType, Function.identity()));
        this.oauth2TokenServiceRegistry = oauth2TokenServices.stream()
            .collect(Collectors.toMap(OAuth2TokenService::supportGrantType, Function.identity()));
        this.oauth2ClientManager = Objects.requireNonNull(
            oauth2ClientManager, "oauth2ClientManager cannot be null");
        this.oauth2SubjectManager = Objects.requireNonNull(
            oauth2SubjectManager, "oauth2SubjectManager cannot be null");
        this.oauth2TokenRepository = Objects.requireNonNull(
            oauth2TokenRepository, "oauth2TokenRepository cannot be null");
    }

    @Override
    public URI authorize(AuthorizeContext<CERTIFICATE> context) {
        OAuth2AuthorizeService<CERTIFICATE> oauth2AuthorizeService = oauth2AuthorizeServiceRegistry
            .get(context.getResponseType());
        if (oauth2AuthorizeService == null) {
            log.warn("unknown responseType, responseType: {}", context.getResponseType());
            throw OAuth2ErrorCodes.UNKNOWN_RESPONSE_TYPE.asThrowable();
        }
        return oauth2AuthorizeService.authorize(context);
    }

    @Override
    public OAuth2TokenDTO token(TokenContext context) {
        OAuth2TokenService oauth2TokenService = oauth2TokenServiceRegistry
            .get(context.getGrantType());
        if (oauth2TokenService == null) {
            log.warn("unknown grantType, grantType: {}", context.getGrantType());
            throw OAuth2ErrorCodes.UNKNOWN_GRANT_TYPE.asThrowable();
        }
        return oauth2TokenService.token(context);
    }

    @Override
    public SUBJECT subject(String accessToken, String scope) {
        if (StringUtils.isBlank(accessToken)) {
            log.warn("invalid accessToken, accessToken: {}", accessToken);
            throw OAuth2ErrorCodes.INVALID_ACCESS_TOKEN.asThrowable();
        }
        OAuth2Token oauth2Token = oauth2TokenRepository.getByAccessToken(accessToken);
        if (oauth2Token == null) {
            log.warn("invalid accessToken, accessToken: {}", accessToken);
            throw OAuth2ErrorCodes.INVALID_ACCESS_TOKEN.asThrowable();
        }

        OAuth2Client client = oauth2ClientManager.getClientRequired(
            oauth2Token.getClientId());

        if (oauth2Token.accessTokenExpired(client.getRefreshTokenExpireSeconds())) {
            log.warn("accessToken expired, accessToken: {}", oauth2Token.getAccessToken());
            throw OAuth2ErrorCodes.ACCESS_TOKEN_EXPIRED.asThrowable();
        }
        if (oauth2Token.authorizationExpired(client.getAuthorizeExpireSeconds())) {
            log.warn("authorization expired, refreshToken: {}", oauth2Token.getRefreshToken());
            throw OAuth2ErrorCodes.AUTHORIZATION_EXPIRED.asThrowable();
        }

        Set<String> scopeUnits = new HashSet<>(OAuth2ScopeUtils.splitScope(oauth2Token.getScope()));
        if (StringUtils.isNotBlank(scope)) {
            Set<String> specifiedScopeUnits = OAuth2ScopeUtils.splitScope(scope);
            scopeUnits.retainAll(specifiedScopeUnits);
        }
        Result<SUBJECT> result = oauth2SubjectManager.getSubject(client, oauth2Token.getSubjectId(), scopeUnits);
        if (!result.isSuccess()) {
            log.warn("get subject failed, message: {}, accessToken: {}",
                result.getMessage(), oauth2Token.getAccessToken());
            throw result.getErrorCode().asThrowable();
        }

        return result.getData();
    }

    @Override
    public void revokeToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            log.warn("invalid accessToken, accessToken: {}", accessToken);
            throw OAuth2ErrorCodes.INVALID_ACCESS_TOKEN.asThrowable();
        }
        oauth2TokenRepository.removeByAccessToken(accessToken);
    }

}
