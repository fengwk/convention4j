package fun.fengwk.convention4j.oauth2.server.model.context;

import fun.fengwk.convention4j.common.web.UriUtils;
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

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = UriUtils.decodeUriComponent(redirectUri);
    }

}
