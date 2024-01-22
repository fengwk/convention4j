package fun.fengwk.convention4j.oauth2.sdk.client;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.sdk.client.model.AuthorizeParams;

/**
 * @author fengwk
 */
public interface AuthorizeClient<CERTIFICATE> {

    /**
     * authorize请求
     */
    Result<String> authorize(AuthorizeParams<CERTIFICATE> params);

}
