//package fun.fengwk.convention4j.springboot.starter.web.result;
//
//import fun.fengwk.convention4j.api.code.Code;
//import fun.fengwk.convention4j.api.code.CommonErrorCodes;
//import fun.fengwk.convention4j.api.code.ConventionErrorCode;
//import fun.fengwk.convention4j.api.code.ImmutableConventionErrorCode;
//import fun.fengwk.convention4j.api.result.Result;
//import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
//import fun.fengwk.convention4j.common.result.Results;
//import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.core.annotation.AnnotationAwareOrderComparator;
//import org.springframework.http.*;
//import org.springframework.lang.Nullable;
//import org.springframework.util.CollectionUtils;
//import org.springframework.validation.FieldError;
//import org.springframework.web.ErrorResponse;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingPathVariableException;
//import org.springframework.web.context.request.ServletWebRequest;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//import org.springframework.web.util.WebUtils;
//
//import java.io.IOException;
//import java.util.*;
//
//import static fun.fengwk.convention4j.api.code.CommonErrorCodes.BAD_REQUEST;
//import static fun.fengwk.convention4j.api.code.CommonErrorCodes.INTERNAL_SERVER_ERROR;
//
///**
// * @author fengwk
// */
//@Slf4j
//public class WebHandlerExceptionResolver extends ResponseEntityExceptionHandler
//    implements HandlerExceptionResolver {
//
//    private final List<WebExceptionResultHandler> chain;
//
//    public WebHandlerExceptionResolver(ObjectProvider<List<WebExceptionResultHandler>> chainProvider) {
//        List<WebExceptionResultHandler> chain = chainProvider.getIfAvailable(Collections::emptyList);
//        AnnotationAwareOrderComparator.sort(chain);
//        this.chain = chain;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("started {}", getClass().getSimpleName());
//    }
//
//    @Override
//    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
//                                         Object handler, Exception ex) {
//        ResponseEntity<Object> responseEntity = doResolveException(request, response, handler, ex);
//        return new ModelAndView();
//    }
//
//    private void adaptResponseEntity(ResponseEntity<Object> responseEntity,
//                                     HttpServletResponse response) {
//        try {
//            // 设置HTTP状态码
//            response.setStatus(responseEntity.getStatusCodeValue());
//
//            // 设置响应头
//            HttpHeaders headers = responseEntity.getHeaders();
//            headers.forEach((key, values) ->
//                values.forEach(value -> response.addHeader(key, value))
//            );
//
//            // 确保内容类型为JSON
//            if (!response.containsHeader(HttpHeaders.CONTENT_TYPE)) {
//                response.setContentType("application/json;charset=UTF-8");
//            }
//
//            // 序列化响应体
//            Object body = responseEntity.getBody();
//            if (body != null) {
//                String json = objectMapper.writeValueAsString(body);
//                response.getWriter().write(json);
//            }
//        } catch (IOException e) {
//            // 处理序列化错误
//            try {
//                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    "Error processing exception");
//            } catch (IOException ioException) {
//                // 记录日志
//            }
//        }
//    }
//
//    private ResponseEntity<Object> doResolveException(HttpServletRequest request, HttpServletResponse response,
//                                                      Object handler, Exception ex) {
//        WebRequest webRequest = new ServletWebRequest(request);
//
//        // 如果不是Result返回值使用默认的方式处理
//        if (!(handler instanceof HandlerMethod handlerMethod)
//            || !Result.class.isAssignableFrom(handlerMethod.getReturnType().getParameterType())) {
//            try {
//                return super.handleException(ex, webRequest);
//            } catch (Exception superHandleEx) {
//                // 如果出现异常说明super.handleException无法处理，继续走下面的处理逻辑
//                log.warn("super.handleException failed: {}", superHandleEx.getMessage());
//            }
//        }
//
//        WebExceptionResultHandlerContext context = new WebExceptionResultHandlerContext();
//        context.setRequest(request);
//        context.setWebRequest(webRequest);
//
//        Result<Void> result = doHandleThrowable(ex, context);
//
//        if (fun.fengwk.convention4j.api.code.HttpStatus.is5xx(result.getStatus())) {
//            error(request, ex);
//        } else {
//            warn(request, ex);
//        }
//
//        if (ResultInternalInvokerUtils.isIgnoreErrorHttpStatus(request::getHeader)) {
//            if (context.getResponseHeaders() != null) {
//                return new ResponseEntity<>(result, context.getResponseHeaders(), HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(result, HttpStatus.OK);
//            }
//        } else {
//            if (context.getResponseHeaders() != null) {
//                return new ResponseEntity<>(result, context.getResponseHeaders(),
//                    HttpStatus.valueOf(result.getStatus()));
//            } else {
//                return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
//            }
//        }
//    }
//
//    /**
//     * 重新定义统一的异常格式为Result
//     */
//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal(
//        Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
//
//        if (request instanceof ServletWebRequest servletWebRequest) {
//            HttpServletResponse response = servletWebRequest.getResponse();
//            if (response != null && response.isCommitted()) {
//                if (logger.isWarnEnabled()) {
//                    logger.warn("Response already committed. Ignoring: " + ex);
//                }
//                return null;
//            }
//        }
//
//        if (body == null && ex instanceof ErrorResponse errorResponse) {
//            ProblemDetail problemDetail = errorResponse.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());
//            body = adaptResultBody(problemDetail);
//        }
//
//        if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
//            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
//        }
//
//        if (ResultInternalInvokerUtils.isIgnoreErrorHttpStatus(request::getHeader)) {
//            return createResponseEntity(body, headers, HttpStatus.OK, request);
//        } else {
//            return createResponseEntity(body, headers, statusCode, request);
//        }
//    }
//
//    private Result<Void> adaptResultBody(ProblemDetail problemDetail) {
//        if (problemDetail == null) {
//            return Results.error(INTERNAL_SERVER_ERROR);
//        }
//
//        ConventionErrorCode errorCode = CommonErrorCodes.ofStatus(problemDetail.getStatus());
//        if (errorCode == null) {
//            errorCode = INTERNAL_SERVER_ERROR;
//        }
//        Map<String, Object> errorContext = new HashMap<>();
//        errorContext.put("type", problemDetail.getType()); // about:blank
//        if (problemDetail.getTitle() != null) { // Not Found
//            errorContext.put("title", problemDetail.getTitle());
//        }
//        if (problemDetail.getDetail() != null) { // No static resource xxx
//            errorContext.put("detail", problemDetail.getDetail());
//        }
//        if (problemDetail.getInstance() != null) {
//            errorContext.put("instance", problemDetail.getInstance());
//        }
//        if (problemDetail.getProperties() != null) {
//            errorContext.put("properties", problemDetail.getProperties());
//        }
//        return Results.error(errorCode.withErrorContext(errorContext));
//    }
//
//    private Result<Void> doHandleThrowable(Throwable ex, WebExceptionResultHandlerContext context) {
//        for (WebExceptionResultHandler handler : chain) {
//            if (handler.support(ex)) {
//                return handler.handle(context);
//            }
//        }
//        return defaultDoHandleThrowable(ex, context);
//    }
//
//    private Result<Void> defaultDoHandleThrowable(Throwable ex, WebExceptionResultHandlerContext context) {
//        ConventionErrorCode retErrorCode = null;
//        Map<String, String> errors = Collections.emptyMap();
//        if (ex instanceof HttpRequestMethodNotSupportedException methodNotSupportEx) {
//            HttpHeaders headers = new HttpHeaders();
//            Set<HttpMethod> supportedMethods = methodNotSupportEx.getSupportedHttpMethods();
//            if (!CollectionUtils.isEmpty(supportedMethods)) {
//                headers.setAllow(supportedMethods);
//            }
//            context.setResponseHeaders(headers);
//            retErrorCode = CommonErrorCodes.ofStatus(methodNotSupportEx.getStatusCode().value());
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
//        } else if (ex instanceof HttpMediaTypeNotSupportedException mediaTypeNotSupportedEx) {
//            HttpHeaders headers = new HttpHeaders();
//            List<MediaType> mediaTypes = mediaTypeNotSupportedEx.getSupportedMediaTypes();
//            if (!CollectionUtils.isEmpty(mediaTypes)) {
//                headers.setAccept(mediaTypes);
//            }
//            context.setResponseHeaders(headers);
//            retErrorCode = CommonErrorCodes.ofStatus(mediaTypeNotSupportedEx.getStatusCode().value());
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
//        } else if (ex instanceof MissingPathVariableException pathEx) {
//            if (pathEx.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
//                context.getWebRequest().setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
//            }
//            retErrorCode = CommonErrorCodes.ofStatus(pathEx.getStatusCode().value());
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
//        } else if (ex instanceof MethodArgumentNotValidException argEx) {
//            // 如果在Controller中使用@Valid注释的Bean对象，该校验动作将在SpringMVC过程中处理，抛出MethodArgumentNotValidException异常
//            retErrorCode = CommonErrorCodes.ofStatus(argEx.getStatusCode().value());
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
//            errors = convertToErrors(argEx);
//        } else if (ex instanceof IllegalArgumentException) {
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex);
//        } else if (ex instanceof ConventionErrorCode conventionErrorCode) {
//            retErrorCode = conventionErrorCode;
//        } else if (ex instanceof ErrorResponse er) {
//            retErrorCode = CommonErrorCodes.ofStatus(er.getStatusCode().value());
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(retErrorCode, ex);
//        } else if (ex instanceof Code code) {
//            retErrorCode = new ImmutableConventionErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                code.getCode(), code.getMessage(), Collections.emptyMap());
//        }
//        if (retErrorCode == null) {
//            retErrorCode = ResultExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex);
//        }
//        return Results.error(retErrorCode, errors);
//    }
//
//    private void warn(HttpServletRequest request, Throwable ex) {
//        log.warn("request failed, request: {}, error: {}", formatRequest(request), String.valueOf(ex));
//    }
//
//    private void error(HttpServletRequest request, Throwable ex) {
//        log.error("request failed, request: '{}'", formatRequest(request), ex);
//    }
//
//    private String formatRequest(HttpServletRequest request) {
//        return request.getMethod() + " " + request.getRequestURI()
//            + (request.getQueryString() == null || request.getQueryString().isEmpty() ? "" : "?" + request.getQueryString());
//    }
//
//    private Map<String, String> convertToErrors(MethodArgumentNotValidException ex) {
//        Map<String, String> map = new HashMap<>();
//        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
//            map.put(fe.getObjectName() + "." + fe.getField(), fe.getDefaultMessage());
//        }
//        return map;
//    }
//
//}
