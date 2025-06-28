package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.transport.TransportHeaders;
import fun.fengwk.convention4j.springboot.starter.web.context.WebContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author fengwk
 */
public class TransportHeadersFeignClientInterceptor implements RequestInterceptor {

    private final WebContext webContext;
    private final TransportHeaders transportHeaders;

    public TransportHeadersFeignClientInterceptor(WebContext webContext,
                                                  TransportHeaders transportHeaders) {
        this.webContext = Objects.requireNonNull(webContext, "webContext must not be null");
        this.transportHeaders = Objects.requireNonNull(transportHeaders, "transportHeaders must not be null");
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = webContext.getRequest();
        if (request == null) {
            return;
        }
        for (String headerName : transportHeaders.viewHeaders()) {
            String headerValue = request.getHeader(headerName);
            if (StringUtils.isNotBlank(headerValue)) {
                template.header(headerName, headerValue);
            }
        }
    }

}
