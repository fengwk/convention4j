package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * @author fengwk
 */
public class InternalInvokerWebClientRequestModifier implements WebClientRequestModifier {

    @Override
    public void modify(WebFluxContext context, ClientRequest.Builder requestBuilder) {
        ResultInternalInvokerUtils.setIgnoreErrorHttpStatus((name, value) ->
            requestBuilder.headers(headers -> headers.set(name, value)));
    }

}
