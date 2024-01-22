package fun.fengwk.convention4j.oauth2.sdk.client;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

/**
 * @author fengwk
 */
public interface RefreshTokenClient {

    /**
     * refreshToken请求
     */
    Result<OAuth2TokenDTO> refreshToken(String refreshToken);

}
