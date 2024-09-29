package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ScopeUtils;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.SsoContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import lombok.extern.slf4j.Slf4j;

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
            OAuth2Token oauth2Token = oauth2TokenRepository.getBySsoId(ssoContext.getSsoId());
            // 如果使用ssoId获取到了oauth2令牌，且作用域相同，且令牌的认证尚未到期，则允许使用该令牌进行sso
            if (oauth2Token != null
                && sameScope(oauth2Token.getScope(), scope)
                && !oauth2Token.authorizeExpired(client.getAuthorizeExpireSeconds())) {
                // 尝试获取subject，如果成功则sso成功
                String subjectId = oauth2Token.getSubjectId();
                Result<SUBJECT> result = oauth2SubjectManager.getSubject(client, subjectId, OAuth2ScopeUtils.splitScope(scope));
                if (!result.isSuccess()) {
                    log.error("Get subject failed, result: {}, clientId: {}, subjectId: {}, scope: {}",
                        result, client.getClientId(), subjectId, scope);
                } else if (result.getData() != null) {
                    // 如果成功获取到了subject说明sso成功，直接返回subjectId即可
                    ssoContext.setSsoAuthenticate(true);
                    return subjectId;
                }
            }
        }
        // 标准的认证流程
        Result<String> result = oauth2SubjectManager.authenticate(client, certificate);
        if (result.getData() == null) {
            log.warn("Authenticate failed, result: {}, clientId: {}, certificate: {}",
                result, client.getClientId(), certificate);
            ConventionErrorCode errorCode;
            if (!result.isSuccess()) {
                errorCode = result.getErrorCode();
            } else {
                errorCode = OAuth2ErrorCodes.AUTHENTICATE_FAILED;
            }
            throw errorCode.asThrowable();
        }
        return result.getData();
    }

    protected void checkScope(OAuth2Client client, String scope) {
        if (!client.supportScope(scope)) {
            log.warn("Unsupported scope, clientId: {}, scope: {}", client.getClientId(), scope);
            throw OAuth2ErrorCodes.UNSUPPORTED_SCOPE.asThrowable();
        }
    }

    protected OAuth2Token generateToken(String subjectId, String scope, OAuth2Client client, String ssoId) {
        OAuth2Token oauth2Token = OAuth2Token.generate(
            oauth2TokenRepository.generateId(), client.getClientId(), subjectId, scope, ssoId);
        if (!oauth2TokenRepository.add(oauth2Token, client.getAuthorizeExpireSeconds())) {
            log.warn("Generate oauth2 token failed, oauth2Token: {}", oauth2Token);
            throw OAuth2ErrorCodes.GENERATE_OAUTH2_TOKEN_FAILED.asThrowable();
        }
        return oauth2Token;
    }

    protected String getSsoId(Object context) {
        String ssoId;
        if (context instanceof SsoContext ssoContext) {
            if (ssoContext.isSsoAuthenticate()) {
                ssoId = ssoContext.getSsoId();
            } else {
                ssoId = generateSsoId();
                ssoContext.setSsoId(ssoId);
            }
        } else {
            ssoId = generateSsoId();
        }
        return ssoId;
    }

    private static String generateSsoId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return sb.toString();
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
