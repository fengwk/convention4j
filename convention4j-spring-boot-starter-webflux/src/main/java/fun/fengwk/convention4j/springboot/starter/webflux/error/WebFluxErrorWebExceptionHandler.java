package fun.fengwk.convention4j.springboot.starter.webflux.error;

import fun.fengwk.convention4j.api.code.Code;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ImmutableConventionErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.starter.webflux.util.ReactiveResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fun.fengwk.convention4j.api.code.CommonErrorCodes.INTERNAL_SERVER_ERROR;
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

        Result<Void> result;
        if (ex instanceof NoResourceFoundException notFoundEx) {
            log.error("Handle error request, {}, method: {}, path: {}, remoteAddress: {}",
                notFoundEx.getMessage(), request.method().name(), request.path(), request.remoteAddress().orElse(null));
            result = defaultDoHandleThrowable(notFoundEx);
        } else if (ex instanceof Throwable t) {
            log.error("Handle error request, method: {}, path: {}, remoteAddress: {}",
                request.method().name(), request.path(), request.remoteAddress().orElse(null), t);
            result = defaultDoHandleThrowable(t);
        } else {
            log.error("Handle error request, method: {}, path: {}, remoteAddress: {}",
                request.method().name(), request.path(), request.remoteAddress().orElse(null));
            result = Results.error(INTERNAL_SERVER_ERROR);
        }

        return ReactiveResultUtils.adapt(request, result);
    }

    @Override
    public int getOrder() {
        // 必须要<=这个位置，否则无法生效
        return -1;
    }

    private Result<Void> defaultDoHandleThrowable(Throwable ex) {
        ConventionErrorCode retErrorCode = null;
        Map<String, String> errors = Collections.emptyMap();
        if (ex instanceof MethodArgumentNotValidException argEx) {
            // 如果在Controller中使用@Valid注释的Bean对象，该校验动作将在SpringMVC过程中处理，抛出MethodArgumentNotValidException异常
            retErrorCode = CommonErrorCodes.ofStatus(argEx.getStatusCode().value());
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
            errors = convertToErrors(argEx);
        } if (ex instanceof ConventionErrorCode conventionErrorCode) {
            retErrorCode = conventionErrorCode;
        } else if (ex instanceof ErrorResponse er) {
            retErrorCode = CommonErrorCodes.ofStatus(er.getStatusCode().value());
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
        } else if (ex instanceof Code code) {
            retErrorCode = new ImmutableConventionErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                code.getCode(), code.getMessage(), Collections.emptyMap());
        }
        if (retErrorCode == null) {
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex);
        }
        return Results.error(retErrorCode, errors);
    }

    private Map<String, String> convertToErrors(MethodArgumentNotValidException ex) {
        Map<String, String> map = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            map.put(fe.getObjectName() + "." + fe.getField(), fe.getDefaultMessage());
        }
        return map;
    }

}
