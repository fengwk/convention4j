package fun.fengwk.convention4j.oauth2.server.model.context;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class DefaultTokenContext<CERTIFICATE> implements AuthenticationCodeTokenContext, ClientCredentialsTokenContext,
    PasswordTokenContext<CERTIFICATE>, RefreshTokenContext, SsoContext {

    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String subjectId;
    private String refreshToken;
    private CERTIFICATE certificate;
    private String ssoId;
    private boolean ssoAuthenticate;

}
