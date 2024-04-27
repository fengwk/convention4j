package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import fun.fengwk.convention4j.springboot.starter.webflux.tracer.TracerXHeader;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * @author fengwk
 */
public class XHeaderWebClientRequestModifier implements WebClientRequestModifier {

    @Override
    public void modify(WebFluxContext webFluxContext, ClientRequest.Builder builder) {
        for (TracerXHeader xHeader : TracerXHeader.values()) {
            String headerName = xHeader.getName();
            String headerValue = webFluxContext.getRequest().getHeaders().getFirst(headerName);
            if (StringUtils.isNotBlank(headerValue)) {
                builder.header(headerName, headerValue);
            }
        }
    }

}
