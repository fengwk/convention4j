package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author fengwk
 */
public class ImplicitMode<SUBJECT, CERTIFICATE> extends BaseOAuth2AuthorizeService<SUBJECT, CERTIFICATE> {

    public ImplicitMode(OAuth2ClientManager clientManager,
                           OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                           OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    @Override
    protected ResponseType getResponseType() {
        return OAuth2Mode.IMPLICIT.getResponseType();
    }

    @Override
    protected URI generateAuthorizeUri(AuthorizeContext<CERTIFICATE> context,
                                       OAuth2Client client,
                                       UriComponentsBuilder redirectUriBuilder,
                                       String subjectId) {
        String ssoId = getSsoId(context);
        OAuth2Token oauth2TokenDTO = generateToken(subjectId, context.getScope(), client, ssoId);
        return buildAuthorizeURI(redirectUriBuilder, oauth2TokenDTO, context.getState(), client);
    }

    private URI buildAuthorizeURI(UriComponentsBuilder redirectUriBuilder,
                                  OAuth2Token oauth2Token,
                                  String state,
                                  OAuth2Client client) {
        return redirectUriBuilder
            .queryParam("accessToken", oauth2Token.getAccessToken())
            .queryParam("tokenType", NullSafe.map(oauth2Token.getTokenType(), TokenType::getCode))
            .queryParam("expiresIn",oauth2Token.accessTokenExpiresIn(client.getAccessTokenExpireSeconds()))
            .queryParam("state", state)
            .build().toUri();
    }

}
