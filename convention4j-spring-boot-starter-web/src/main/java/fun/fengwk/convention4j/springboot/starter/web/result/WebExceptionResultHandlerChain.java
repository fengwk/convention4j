package fun.fengwk.convention4j.springboot.starter.web.result;

import fun.fengwk.convention4j.api.code.Code;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ImmutableConventionErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.*;

import static fun.fengwk.convention4j.api.code.CommonErrorCodes.INTERNAL_SERVER_ERROR;

/**
 * REST协议的异常处理程序。
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @author fengwk
 */
@RestControllerAdvice
public class WebExceptionResultHandlerChain /*extends ResponseEntityExceptionHandler*/ {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionResultHandlerChain.class);

    private final List<WebExceptionResultHandler> chain;

    public WebExceptionResultHandlerChain(ObjectProvider<List<WebExceptionResultHandler>> chainProvider) {
        List<WebExceptionResultHandler> chain = chainProvider.getIfAvailable(Collections::emptyList);
        AnnotationAwareOrderComparator.sort(chain);
        this.chain = chain;
    }

    @PostConstruct
    public void init() {
        log.info("started {}", getClass().getSimpleName());
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleThrowable(
            Exception ex, HttpServletRequest request, WebRequest webRequest/*, HandlerMethod handlerMethod*/) /*throws Exception*/ {

        // 如果不是Result返回值使用默认的方式处理
        //
//        if (!Result.class.isAssignableFrom(handlerMethod.getReturnType().getParameterType())) {
//            return super.handleException(ex, webRequest);
//        }

        WebExceptionResultHandlerContext context = new WebExceptionResultHandlerContext();
        context.setRequest(request);
        context.setWebRequest(webRequest);

        Result<Void> result = doHandleThrowable(ex, context);

        if (fun.fengwk.convention4j.api.code.HttpStatus.is5xx(result.getStatus())) {
            error(request, ex);
        } else {
            warn(request, ex);
        }

        if (ResultInternalInvokerUtils.isIgnoreErrorHttpStatus(request::getHeader)) {
            if (context.getResponseHeaders() != null) {
                return new ResponseEntity<>(result, context.getResponseHeaders(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } else {
            if (context.getResponseHeaders() != null) {
                return new ResponseEntity<>(result, context.getResponseHeaders(),
                    HttpStatus.valueOf(result.getStatus()));
            } else {
                return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
            }
        }
    }

    private Result<Void> doHandleThrowable(Throwable ex, WebExceptionResultHandlerContext context) {
        for (WebExceptionResultHandler handler : chain) {
            if (handler.support(ex)) {
                return handler.handle(context);
            }
        }
        return defaultDoHandleThrowable(ex, context);
    }

    private Result<Void> defaultDoHandleThrowable(Throwable ex, WebExceptionResultHandlerContext context) {
        ConventionErrorCode retErrorCode = null;
        Map<String, String> errors = Collections.emptyMap();
        if (ex instanceof HttpRequestMethodNotSupportedException methodNotSupportEx) {
            HttpHeaders headers = new HttpHeaders();
            Set<HttpMethod> supportedMethods = methodNotSupportEx.getSupportedHttpMethods();
            if (!CollectionUtils.isEmpty(supportedMethods)) {
                headers.setAllow(supportedMethods);
            }
            context.setResponseHeaders(headers);
            retErrorCode = CommonErrorCodes.ofStatus(methodNotSupportEx.getStatusCode().value());
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
        } else if (ex instanceof HttpMediaTypeNotSupportedException mediaTypeNotSupportedEx) {
            HttpHeaders headers = new HttpHeaders();
            List<MediaType> mediaTypes = mediaTypeNotSupportedEx.getSupportedMediaTypes();
            if (!CollectionUtils.isEmpty(mediaTypes)) {
                headers.setAccept(mediaTypes);
            }
            context.setResponseHeaders(headers);
            retErrorCode = CommonErrorCodes.ofStatus(mediaTypeNotSupportedEx.getStatusCode().value());
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
        } else if (ex instanceof MissingPathVariableException pathEx) {
            if (pathEx.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                context.getWebRequest().setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
            }
            retErrorCode = CommonErrorCodes.ofStatus(pathEx.getStatusCode().value());
            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
        } else if (ex instanceof MethodArgumentNotValidException argEx) {
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

    private void warn(HttpServletRequest request, Throwable ex) {
        log.warn("request failed, request: {}, error: {}", formatRequest(request), String.valueOf(ex));
    }

    private void error(HttpServletRequest request, Throwable ex) {
        log.error("request failed, request: '{}'", formatRequest(request), ex);
    }
    
    private String formatRequest(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI()
                + (request.getQueryString() == null || request.getQueryString().isEmpty() ? "" : "?" + request.getQueryString());
    }

    private Map<String, String> convertToErrors(MethodArgumentNotValidException ex) {
        Map<String, String> map = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            map.put(fe.getObjectName() + "." + fe.getField(), fe.getDefaultMessage());
        }
        return map;
    }

}
