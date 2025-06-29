package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.ClientCredentialsTokenContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;

/**
 * @author fengwk
 */
public class ClientCredentialsMode<SUBJECT, CERTIFICATE>
    extends BaseOAuth2TokenService<SUBJECT, CERTIFICATE, ClientCredentialsTokenContext> {

    public ClientCredentialsMode(OAuth2ClientManager clientManager,
                                    OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                                    OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return OAuth2Mode.CLIENT_CREDENTIALS.getGrantType();
    }

    @Override
    protected OAuth2Token generateOAuth2Token(ClientCredentialsTokenContext context, OAuth2Client client) {
        checkScope(client, context.getScope());
        return reuseOrGenerateOAuth2Token(client, context, context.getSubjectId(), context.getScope());
    }

}
