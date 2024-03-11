package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public abstract class BaseOAuth2TokenService<SUBJECT, CERTIFICATE, CTX extends TokenContext>
    extends BaseOAuth2Service<SUBJECT, CERTIFICATE>
    implements OAuth2TokenService<CTX> {

    protected BaseOAuth2TokenService(OAuth2ClientManager clientManager,
                                     OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                                     OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    /**
     * 获取当前服务支持的GrantType
     */
    protected abstract GrantType getGrantType();

    /**
     * 令牌服务实现
     */
    protected abstract OAuth2Token generateOAuth2Token(CTX context, OAuth2Client client);

    @Override
    public String supportGrantType() {
        return getGrantType().getCode();
    }

    @Override
    public OAuth2TokenDTO token(CTX context) {
        OAuth2Client client = clientManager.getClientRequired(context.getClientId());
        checkGrantType(client);
        checkClientSecret(client, context.getClientSecret());
        OAuth2Token oauth2Token = generateOAuth2Token(context, client);
        return NullSafe.map(oauth2Token, token -> token.toDTO(client));
    }

    private void checkGrantType(OAuth2Client client) {
        if (!client.supportGrantType(getGrantType())) {
            log.warn("Client unsupported grantType, clientId: {}, grantType: {}",
                client.getClientId(), getGrantType());
            throw OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE.asThrowable();
        }
    }

    private void checkClientSecret(OAuth2Client client, String clientSecret) {
        if (!client.validateSecret(clientSecret)) {
            log.warn("Invalid clientSecret, clientId: {}, clientSecret: {}",
                client.getClientId(), clientSecret);
            throw OAuth2ErrorCodes.INVALID_CLIENT_SECRET.asThrowable();
        }
    }

}
