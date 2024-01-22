package fun.fengwk.convention4j.oauth2.core.model.context;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class DefaultAuthorizeContext<CERTIFICATE> implements AuthorizeContext<CERTIFICATE>, SsoContext {

    private String responseType;
    private String clientId;
    private String redirectUri;
    private String scope;
    private String state;
    private CERTIFICATE certificate;
    private String ssoId;
    private boolean ssoAuthenticate;

}
