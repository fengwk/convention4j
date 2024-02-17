package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.oauth2.server.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;

/**
 * @author fengwk
 */
public interface OAuth2TokenService<CTX extends TokenContext> {

    /**
     * 获取当前服务支持的grantType
     */
    String supportGrantType();

    OAuth2TokenDTO token(CTX context);

}
