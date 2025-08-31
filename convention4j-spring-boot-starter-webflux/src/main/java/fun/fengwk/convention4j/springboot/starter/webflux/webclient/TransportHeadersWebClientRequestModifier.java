package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.transport.TransportHeaders;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.util.Objects;

/**
 * @author fengwk
 */
public class TransportHeadersWebClientRequestModifier implements WebClientRequestModifier {

    private final TransportHeaders transportHeaders;

    public TransportHeadersWebClientRequestModifier(TransportHeaders transportHeaders) {
        this.transportHeaders = Objects.requireNonNull(
            transportHeaders, "transportHeaders must not be null");
    }

    @Override
    public void modify(WebFluxContext webFluxContext, ClientRequest.Builder builder) {
        for (String headerName : transportHeaders.viewHeaders()) {
            String headerValue = webFluxContext.getExchange().getRequest().getHeaders().getFirst(headerName);
            if (StringUtils.isNotBlank(headerValue)) {
                builder.headers(headers -> headers.set(headerName, headerValue));
            }
        }
    }

}
