package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.PasswordTokenContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;

/**
 * @author fengwk
 */
public class PasswordMode<SUBJECT, CERTIFICATE>
    extends BaseOAuth2TokenService<SUBJECT, CERTIFICATE, PasswordTokenContext<CERTIFICATE>> {

    public PasswordMode(OAuth2ClientManager clientManager,
                           OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                           OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return OAuth2Mode.PASSWORD.getGrantType();
    }

    @Override
    protected OAuth2Token generateOAuth2Token(PasswordTokenContext<CERTIFICATE> context, OAuth2Client client) {
        checkScope(client, context.getScope());
        String subjectId = authenticate(client, context.getCertificate(), context.getScope(), context);
        String ssoId = getSsoId(context);
        return generateToken(subjectId, context.getScope(), client, ssoId);
    }

}
