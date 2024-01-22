package fun.fengwk.convention4j.oauth2.sdk.client.model;

import lombok.Data;

/**
 * token请求参数
 *
 * @author fengwk
 */
@Data
public class TokenParams<CERTIFICATE> {

    private String grantType;
    private String code;
    private String redirectUri;
    private String scope;
    private String subjectId;
    private String refreshToken;
    private CERTIFICATE certificate;

}
