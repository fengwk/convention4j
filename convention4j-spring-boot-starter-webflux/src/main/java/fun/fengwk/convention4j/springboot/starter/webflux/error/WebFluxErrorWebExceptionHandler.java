package fun.fengwk.convention4j.springboot.starter.webflux.error;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.starter.webflux.util.ReactiveResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author fengwk
 * @see <a href="https://stackoverflow.com/questions/60139167/string-insted-of-whitelabel-error-page-in-webflux">string-insted-of-whitelabel-error-page-in-webflux</a>
 */
@Slf4j
public class WebFluxErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler implements Ordered {

    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public WebFluxErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                           WebProperties.Resources resources,
                                           ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Object ex = request
            .attribute("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR")
            .orElse(null);

        if (ex instanceof Throwable t) {
            log.error("Handle error request, method: {}, path: {}, remoteAddress: {}",
                request.method().name(), request.path(), request.remoteAddress().orElse(null), t);
        } else {
            log.error("Handle error request, method: {}, path: {}, remoteAddress: {}",
                request.method().name(), request.path(), request.remoteAddress().orElse(null));
        }

        Result<Void> result = Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);

        if (ex instanceof ErrorResponseException respEx) {
            int status = respEx.getStatusCode().value();
            for (CommonErrorCodes code : CommonErrorCodes.values()) {
                if (code.getStatus() == status) {
                    result = Results.error(CommonErrorCodes.NOT_FOUND);
                    break;
                }
            }
        }

        if (ex instanceof ConventionErrorCode ecEx) {
            result = Results.error(ecEx);
        }

        return ReactiveResultUtils.adapt(result);
    }

    @Override
    public int getOrder() {
        // 必须要<=这个位置，否则无法生效
        return -1;
    }

}
