package fun.fengwk.convention4j.oauth2.core.service.mode;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.core.model.context.SsoContext;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
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

    protected String authenticate(OAuth2Client client, CERTIFICATE certificate, Object context) {
        if (client.isAllowSso() && context instanceof SsoContext ssoContext && ssoContext.getSsoId() != null) {
            // 如果当前客户端允许sso，并且当前上下文中存在ssoId，则尝试使用ssoId进行认证
            OAuth2Token oauth2Token = oauth2TokenRepository.getBySsoId(ssoContext.getSsoId());
            // 如果使用ssoId获取到了oauth2令牌，且令牌的认证尚未到期则允许使用该令牌进行sso
            if (oauth2Token != null && !oauth2Token.authorizationExpired(client.getAuthorizeExpireSeconds())) {
                ssoContext.setSsoAuthenticate(true);
                return oauth2Token.getSubjectId();
            }
        }
        // 标准的认证流程
        Result<String> result = oauth2SubjectManager.authenticate(client, certificate);
        if (result.getData() == null) {
            log.warn("authenticate failed, message: {}, clientId: {}, certificate: {}",
                result.getMessage(), client.getClientId(), GsonUtils.toJson(certificate));
            ErrorCode errorCode;
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
            log.warn("unsupported scope, clientId: {}, scope: {}", client.getClientId(), scope);
            throw OAuth2ErrorCodes.UNSUPPORTED_SCOPE.asThrowable();
        }
    }

    protected OAuth2Token generateToken(String subjectId, String scope, OAuth2Client client, String ssoId) {
        OAuth2Token oauth2Token = OAuth2Token.generate(
            oauth2TokenRepository.generateId(), client.getClientId(), subjectId, scope, ssoId);
        if (!oauth2TokenRepository.add(oauth2Token)) {
            log.warn("generate oauth2 token failed, oauth2Token: {}", oauth2Token);
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

}
