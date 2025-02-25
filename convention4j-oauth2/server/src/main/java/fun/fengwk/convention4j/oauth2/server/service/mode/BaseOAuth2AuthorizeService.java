package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.web.UriUtils;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author fengwk
 */
@Slf4j
public abstract class BaseOAuth2AuthorizeService<SUBJECT, CERTIFICATE>
    extends BaseOAuth2Service<SUBJECT, CERTIFICATE>
    implements OAuth2AuthorizeService<CERTIFICATE> {

    protected BaseOAuth2AuthorizeService(OAuth2ClientManager clientManager,
                                         OAuth2SubjectManager<SUBJECT, CERTIFICATE> subjectManager,
                                         OAuth2TokenRepository oauth2TokenRepository) {
        super(clientManager, subjectManager, oauth2TokenRepository);
    }

    /**
     * 获取当前服务支持的GrantType
     */
    protected abstract ResponseType getResponseType();

    /**
     * 构建授权uri
     */
    protected abstract URI generateAuthorizeUri(AuthorizeContext<CERTIFICATE> context,
                                                OAuth2Client client,
                                                UriComponentsBuilder redirectUriBuilder,
                                                String subjectId);

    @Override
    public String supportResponseType() {
        return getResponseType().getCode();
    }

    @Override
    public URI authorize(AuthorizeContext<CERTIFICATE> context) {
        OAuth2Client client = clientManager.getClientRequired(context.getClientId());
        checkResponseType(client);
        checkScope(client, context.getScope());
        UriComponentsBuilder redirectUriBuilder = checkAndGetRedirectUriBuilder(client, context.getRedirectUri());
        String subjectId = authenticate(client, context.getCertificate(), context.getScope(), context);
        return generateAuthorizeUri(context, client, redirectUriBuilder, subjectId);
    }

    private void checkResponseType(OAuth2Client client) {
        if (!client.supportResponseType(getResponseType())) {
            log.warn("Client unsupported responseType, clientId: {}, responseType: {}",
                client.getClientId(), getResponseType());
            throw OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE.asThrowable();
        }
    }

    private UriComponentsBuilder checkAndGetRedirectUriBuilder(OAuth2Client client, String redirectUri) {
        UriComponentsBuilder redirectUriBuilder;
        try {
            redirectUriBuilder = UriComponentsBuilder.fromUriString(redirectUri);
            if (StringUtils.isBlank(redirectUriBuilder.build().getScheme())) {
                // 如果无法解析schema可能是因为redirectUri是编码过的，解码后重新构建
                redirectUriBuilder = UriComponentsBuilder.fromUriString(UriUtils.fullDecodeUriComponent(redirectUri));
            }
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid redirectUri, redirectUri: {}", redirectUri);
            throw OAuth2ErrorCodes.INVALID_REDIRECT_URI.asThrowable();
        }
        if (!client.supportRedirectUri(redirectUri)) {
            log.warn("Client unsupported redirectUri, clientId: {}, clientRedirectUris: {}, redirectUri: {}, ",
                client.getClientId(), client.getRedirectUris(), redirectUri);
            throw OAuth2ErrorCodes.UNSUPPORTED_REDIRECT_URI.asThrowable();
        }
        return redirectUriBuilder;
    }

}
