package fun.fengwk.convention4j.oauth2.server.service;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ScopeUtils;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.server.service.mode.OAuth2AuthorizeService;
import fun.fengwk.convention4j.oauth2.server.service.mode.OAuth2TokenService;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        this.oauth2AuthorizeServiceRegistry = NullSafe.of(oauth2AuthorizeServices).stream()
            .collect(Collectors.toMap(OAuth2AuthorizeService::supportResponseType, Function.identity()));
        this.oauth2TokenServiceRegistry = NullSafe.of(oauth2TokenServices).stream()
            .collect(Collectors.toMap(OAuth2TokenService::supportGrantType, Function.identity()));
        this.oauth2ClientManager = oauth2ClientManager;
        this.oauth2SubjectManager = oauth2SubjectManager;
        this.oauth2TokenRepository = oauth2TokenRepository;
    }

    @Override
    public URI authorize(AuthorizeContext<CERTIFICATE> context) {
        Assert.notNull(context, "Context must not be null");
        Assert.notNull(context.getResponseType(), "Response type must not be null");
        OAuth2AuthorizeService<CERTIFICATE> oauth2AuthorizeService = oauth2AuthorizeServiceRegistry
            .get(context.getResponseType());
        Assert.notNull(oauth2AuthorizeService, "Unsupported response type: " + context.getResponseType());
        return oauth2AuthorizeService.authorize(context);
    }

    @Override
    public OAuth2TokenDTO token(TokenContext context) {
        Assert.notNull(context, "Context must not be null");
        Assert.notNull(context.getGrantType(), "Grant type must not be null");
        OAuth2TokenService oauth2TokenService = oauth2TokenServiceRegistry.get(context.getGrantType());
        Assert.notNull(oauth2TokenService, "Unsupported grant type: " + context.getGrantType());
        return oauth2TokenService.token(context);
    }

    @Override
    public SUBJECT subject(String accessToken, String scope) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }

        OAuth2Token oauth2Token = oauth2TokenRepository.getByAccessToken(accessToken);
        if (oauth2Token == null) {
            log.warn("Invalid access token, accessToken: {}", accessToken);
            return null;
        }

        OAuth2Client client = oauth2ClientManager.getClientRequired(oauth2Token.getClientId());

        if (oauth2Token.accessTokenExpired(client.getAccessTokenExpireSeconds())) {
            log.warn("Access token expired, accessToken: {}", oauth2Token.getAccessToken());
            return null;
        }
        if (oauth2Token.authorizeExpired(client.getAuthorizeExpireSeconds())) {
            log.warn("Authorize expired, refreshToken: {}", oauth2Token.getRefreshToken());
            return null;
        }

        Set<String> scopeUnits = new HashSet<>(OAuth2ScopeUtils.splitScope(oauth2Token.getScope()));
        if (StringUtils.isNotBlank(scope)) {
            Set<String> specifiedScopeUnits = OAuth2ScopeUtils.splitScope(scope);
            scopeUnits.retainAll(specifiedScopeUnits);
        }
        Result<SUBJECT> result = oauth2SubjectManager.getSubject(client, oauth2Token.getSubjectId(), scopeUnits);
        return result.getData();
    }

    @Override
    public void revokeToken(String accessToken) {
        Assert.hasText(accessToken, "Invalid access token");
        oauth2TokenRepository.removeByAccessToken(accessToken);
    }

}
