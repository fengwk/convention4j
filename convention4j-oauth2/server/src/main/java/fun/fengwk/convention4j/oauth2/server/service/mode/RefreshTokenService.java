package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.RefreshTokenContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public class RefreshTokenService<SUBJECT, CERTIFICATE>
    extends BaseOAuth2TokenService<SUBJECT, CERTIFICATE, RefreshTokenContext> {

    public RefreshTokenService(OAuth2ClientManager clientManager,
                               OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                               OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.REFRESH_TOKEN;
    }

    @Override
    protected OAuth2Token generateOAuth2Token(RefreshTokenContext context, OAuth2Client client) {
        OAuth2Token oauth2Token = oauth2TokenRepository.getByRefreshToken(context.getRefreshToken());
        if (oauth2Token == null) {
            log.warn("Invalid refreshToken, refreshToken: {}", context.getRefreshToken());
            throw OAuth2ErrorCodes.INVALID_REFRESH_TOKEN.asThrowable();
        }

        if (oauth2Token.refreshTokenExpired(client.getRefreshTokenExpireSeconds())) {
            log.warn("Refresh token expired, refreshToken: {}", oauth2Token.getRefreshToken());
            throw OAuth2ErrorCodes.REFRESH_TOKEN_EXPIRED.asThrowable();
        }
        if (oauth2Token.authorizeExpired(client.getAuthorizeExpireSeconds())) {
            log.warn("Authorize expired, refreshToken: {}", oauth2Token.getRefreshToken());
            throw OAuth2ErrorCodes.AUTHORIZE_EXPIRED.asThrowable();
        }

        oauth2Token.refresh();
        if (!oauth2TokenRepository.updateById(oauth2Token, client.getAuthorizeExpireSeconds())) {
            log.warn("Refresh token failed, clientId: {}, refreshToken: {}",
                client.getClientId(), oauth2Token.getRefreshToken());
            throw OAuth2ErrorCodes.REFRESH_TOKEN_FAILED.asThrowable();
        }
        return oauth2Token;
    }

}
