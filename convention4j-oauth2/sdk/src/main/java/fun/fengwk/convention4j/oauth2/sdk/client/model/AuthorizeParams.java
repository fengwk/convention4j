package fun.fengwk.convention4j.oauth2.sdk.client.model;

import lombok.Data;

/**
 * authorize请求参数
 *
 * @author fengwk
 */
@Data
public class AuthorizeParams<CERTIFICATE> {

    private String responseType;
    private String redirectUri;
    private String scope;
    private String state;
    private CERTIFICATE certificate;

}
