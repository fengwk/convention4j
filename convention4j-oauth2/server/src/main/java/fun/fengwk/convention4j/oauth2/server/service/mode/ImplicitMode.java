package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author fengwk
 */
@Slf4j
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
        OAuth2Token oauth2Token = reuseOrGenerateOAuth2Token(client, context, subjectId, context.getScope());
        return buildAuthorizeURI(redirectUriBuilder, oauth2Token, context.getState(), client);
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
            .build(true).toUri();
    }

}
