package fun.fengwk.convention4j.oauth2.sdk.client;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.sdk.client.model.TokenParams;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

/**
 * OAuth2客户端
 *
 * @author fengwk
 */
public interface TokenClient<CERTIFICATE> {

    /**
     * token请求
     */
    Result<OAuth2TokenDTO> token(TokenParams<CERTIFICATE> params);

}
