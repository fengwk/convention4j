package fun.fengwk.convention4j.oauth2.sdk.client;

import fun.fengwk.convention4j.api.result.Result;

/**
 * @author fengwk
 */
public interface SubjectClient<SUBJECT> {

    /**
     * 获取访问主体
     */
    Result<SUBJECT> subject(String accessToken, String scope);

    /**
     * 获取访问主体
     */
    default Result<SUBJECT> subject(String accessToken) {
        return subject(accessToken, null);
    }

}
