package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.web.UriUtils;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ScopeUtils;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.RedirectUriProvider;
import fun.fengwk.convention4j.oauth2.server.model.context.SsoContext;
import fun.fengwk.convention4j.oauth2.server.model.context.SsoProvider;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author fengwk
 */
@Slf4j
public abstract class BaseOAuth2Service<SUBJECT, CERTIFICATE> {

    protected final OAuth2ClientManager clientManager;
    protected final OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager;
    protected final OAuth2TokenRepository oauth2TokenRepository;

    protected BaseOAuth2Service(OAuth2ClientManager clientManager,
                                OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
                                OAuth2TokenRepository oauth2TokenRepository) {
        this.clientManager = Objects.requireNonNull(
            clientManager, "clientManager cannot be null");
        this.oauth2SubjectManager = Objects.requireNonNull(
            oauth2SubjectManager, "subjectManager cannot be null");
        this.oauth2TokenRepository = Objects.requireNonNull(
            oauth2TokenRepository, "oauth2TokenRepository cannot be null");
    }

    protected String authenticate(OAuth2Client client, CERTIFICATE certificate, String scope, Object context) {
        if (client.isAllowSso() && context instanceof SsoContext ssoContext && ssoContext.getSsoId() != null) {
            // 如果当前客户端允许sso，并且当前上下文中存在ssoId，则尝试使用ssoId进行认证
            List<OAuth2Token> ssoTokens = oauth2TokenRepository.listBySsoId(ssoContext.getSsoId());
            for (OAuth2Token ssoToken : ssoTokens) {
                if (ssoToken != null
                    && sameScope(ssoToken.getScope(), scope)
                    && !ssoToken.authorizeExpired(client.getAuthorizeExpireSeconds())) {
                    String subjectId = ssoToken.getSubjectId();
                    Result<SUBJECT> result = oauth2SubjectManager.getSubject(
                        client, subjectId, OAuth2ScopeUtils.splitScope(scope));
                    if (!result.isSuccess()) {
                        log.error("Get subject failed, result: {}, clientId: {}, subjectId: {}, scope: {}",
                            result, client.getClientId(), subjectId, scope);
                        break;
                    } else if (result.getData() != null) {
                        // 如果成功获取到了subject说明sso成功，设置认证标识位，返回subjectId
                        ssoContext.setSsoAuthenticate(true);
                        return subjectId;
                    }
                }
            }
        }

        // 标准的认证流程
        Result<String> result = oauth2SubjectManager.authenticate(client, certificate);
        if (!result.isSuccess()) {
            log.error("Authenticate failed, result: {}, clientId: {}, certificate: {}",
                result, client.getClientId(), certificate);
            throw result.getErrorCode().asThrowable();
        }
        if (result.getData() == null) {
            log.info("Certificate error, result: {}, clientId: {}, certificate: {}",
                result, client.getClientId(), certificate);
            throw OAuth2ErrorCodes.AUTHENTICATE_FAILED.asThrowable();
        }
        return result.getData();
    }

    protected void checkScope(OAuth2Client client, String scope) {
        if (!client.supportScope(scope)) {
            log.warn("Unsupported scope, clientId: {}, scope: {}", client.getClientId(), scope);
            throw OAuth2ErrorCodes.UNSUPPORTED_SCOPE.asThrowable();
        }
    }

    protected OAuth2Token reuseOrGenerateOAuth2Token(OAuth2Client client, Object context, String subjectId, String scope) {
        OAuth2Token oauth2Token;

        String ssoDomain = null;
        if (context instanceof RedirectUriProvider redirectUriProvider) {
            String redirectUri = redirectUriProvider.getRedirectUri();
            if (redirectUri != null) {
                ssoDomain = parseUriDomain(redirectUri);
            }
        }
        if (ssoDomain == null) {
            ssoDomain = SsoContext.EMPTY_SSO_DOMAIN;
        }

        if (context instanceof SsoProvider ssoProvider && ssoProvider.isSsoAuthenticate()) {
            oauth2Token = getOrGenerateSsoToken(client, subjectId, scope, ssoProvider.getSsoId(), ssoDomain);
        } else {
            String ssoId = null;
            if (context instanceof SsoProvider ssoProvider) {
                ssoId = ssoProvider.getSsoId();
            }
            if (ssoId == null) {
                ssoId = generateSsoId(context);
            }
            oauth2Token = generateAndStoreToken(subjectId, scope, client, ssoId, ssoDomain);
        }

        return oauth2Token;
    }

    protected String generateSsoId(Object context) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(UUID.randomUUID().toString().replace("-", ""));
        }
        String ssoId = sb.toString();
        if (context instanceof SsoContext ssoContext) {
            // 将新生成的ssoId设置到上下文中，以便外部可以用于Cookie设置
            ssoContext.setSsoId(ssoId);
        }
        return ssoId;
    }

    private OAuth2Token getOrGenerateSsoToken(OAuth2Client client, String subjectId, String scope,
                                                String ssoId, String ssoDomain) {
        // 先尝试获取sso域名下的单点登陆令牌
        OAuth2Token ssoToken = oauth2TokenRepository.getBySsoIdAndSsoDomain(ssoId, ssoDomain);

        // 如果该域名下没有单点登陆令牌则使用当前ssoId生成当前域名下的sso令牌
        if (ssoToken == null) {
            return generateAndStoreToken(subjectId, scope, client, ssoId, ssoDomain);
        }

        // 如果访问令牌已过期则刷新，确保返回的令牌是可用的
        if (ssoToken.accessTokenExpired(client.getAccessTokenExpireSeconds())) {
            ssoToken.refresh();
            if (oauth2TokenRepository.updateById(ssoToken, client.getAuthorizeExpireSeconds())) {
                log.debug("SSO token refreshed, ssoToken: {}", ssoToken);
            }
        }
        return ssoToken;
    }

    private String parseUriDomain(String uri) {
        UriComponentsBuilder uriComponentBuilder = toUriComponentBuilder(uri);
        UriComponents uriComponents = uriComponentBuilder.build();
        return uriComponents.getHost();
    }

    protected UriComponentsBuilder toUriComponentBuilder(String uri) {
        UriComponentsBuilder redirectUriBuilder;
        try {
            redirectUriBuilder = UriComponentsBuilder.fromUriString(uri);
            if (StringUtils.isBlank(redirectUriBuilder.build().getScheme())) {
                // 如果无法解析schema可能是因为uri是编码过的，解码后重新构建
                redirectUriBuilder = UriComponentsBuilder.fromUriString(UriUtils.fullDecodeUriComponent(uri));
            }
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid redirectUri, redirectUri: {}", uri);
            throw OAuth2ErrorCodes.INVALID_REDIRECT_URI.asThrowable();
        }
        return redirectUriBuilder;
    }

    private OAuth2Token generateAndStoreToken(String subjectId, String scope, OAuth2Client client,
                                              String ssoId, String ssoDomain) {
        OAuth2Token oauth2Token = OAuth2Token.generate(
            oauth2TokenRepository.generateId(), client.getClientId(), subjectId, scope, ssoId, ssoDomain);
        if (!oauth2TokenRepository.add(oauth2Token, client.getAuthorizeExpireSeconds())) {
            log.warn("Generate oauth2 token failed, oauth2Token: {}", oauth2Token);
            throw OAuth2ErrorCodes.GENERATE_OAUTH2_TOKEN_FAILED.asThrowable();
        }
        return oauth2Token;
    }

    private boolean sameScope(String scope1, String scope2) {
        Set<String> s1 = OAuth2ScopeUtils.splitScope(scope1);
        Set<String> s2 = OAuth2ScopeUtils.splitScope(scope2);
        if (s1.size() != s2.size()) {
            return false;
        }
        for (String s : s1) {
            if (!s2.contains(s)) {
                return false;
            }
        }
        return true;
    }

}
