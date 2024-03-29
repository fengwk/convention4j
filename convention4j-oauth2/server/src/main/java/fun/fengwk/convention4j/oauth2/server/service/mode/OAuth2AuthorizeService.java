package fun.fengwk.convention4j.oauth2.server.service.mode;

import fun.fengwk.convention4j.oauth2.server.model.context.AuthorizeContext;

import java.net.URI;

/**
 * @author fengwk
 */
public interface OAuth2AuthorizeService<CERTIFICATE> {

    /**
     * 获取支持的responseType
     */
    String supportResponseType();

    URI authorize(AuthorizeContext<CERTIFICATE> context);

}
