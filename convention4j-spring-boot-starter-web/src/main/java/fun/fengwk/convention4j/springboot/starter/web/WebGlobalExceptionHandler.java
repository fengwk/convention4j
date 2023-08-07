package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.api.code.ThrowableErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.starter.result.ExceptionHandlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fun.fengwk.convention4j.api.code.CommonErrorCodes.BAD_REQUEST;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.INTERNAL_SERVER_ERROR;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.NOT_ACCEPTABLE;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.NOT_FOUND;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.NOT_IMPLEMENTED;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.SERVICE_UNAVAILABLE;
import static fun.fengwk.convention4j.api.code.CommonErrorCodes.UNSUPPORTED_MEDIA_TYPE;

/**
 * REST协议的异常处理程序。
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @author fengwk
 */
@RestControllerAdvice
public class WebGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebGlobalExceptionHandler.class);

    @PostConstruct
    public void init() {
        log.info("started {}", getClass().getSimpleName());
    }

    // 传入的HTTP请求方法不被允许
    @ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        warn(request, ex);

        // 在响应头中加入当前接口允许的请求方法
        HttpHeaders headers = new HttpHeaders();
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        return new ResponseEntity<>(Results.error(ExceptionHandlerUtils.toErrorCode(NOT_IMPLEMENTED, ex)),
                headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 不支持的媒体类型，比如接口使用了@RequestBody注解要求application/json，但请求却使用了application/x-www-form-urlencoded就会抛出该异常
    @ExceptionHandler(value = { HttpMediaTypeNotSupportedException.class })
    public ResponseEntity<Result<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        warn(request, ex);

        // 在响应头中加入当前接口可接收的媒体类型
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }

        return new ResponseEntity<>(Results.error(ExceptionHandlerUtils.toErrorCode(UNSUPPORTED_MEDIA_TYPE, ex)),
                headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 当接口产生的数据的媒体类型与客户端需要的媒体类型不符时将抛出该异常
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = { HttpMediaTypeNotAcceptableException.class })
    public Result<Void> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(NOT_ACCEPTABLE, ex));
    }

    // 当url中不存在@RequestMapping方法的方法参数中预期的路径变量时将抛出该异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { MissingPathVariableException.class })
    public Result<Void> handleMissingPathVariableException(
            MissingPathVariableException ex, HttpServletRequest request, WebRequest webRequest) {
        warn(request, ex);

        webRequest.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);

        return Results.error(ExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
    }

    // 缺少请求入参产生的异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    public Result<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 参数绑定异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ServletRequestBindingException.class })
    public Result<Void> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 类型转换异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { ConversionNotSupportedException.class })
    public Result<Void> handleConversionNotSupportedException(
            ConversionNotSupportedException ex, HttpServletRequest request) {
        error(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
    }

    // 类型匹配错误
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { TypeMismatchException.class })
    public Result<Void> handleTypeMismatchException(
            TypeMismatchException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 读反序列化时发生异常，例如违反@RequestBody中required约束时将抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { HttpMessageNotReadableException.class })
    public Result<Void> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 写序列化时发生异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { HttpMessageNotWritableException.class })
    public Result<Void> handleHttpMessageNotWritableException(
            HttpMessageNotWritableException ex, HttpServletRequest request) {
        error(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
    }

    // 如果在Controller中使用@Valid注释的Bean对象，该校验动作将在SpringMVC过程中处理，抛出MethodArgumentNotValidException异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public Result<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        warn(request, ex);
        Map<String, String> errors = convertToErrors(ex);
        if (errors.isEmpty()) {
            return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
        } else {
            return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex), errors);
        }
    }

    // 丢失部分请求数据时抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MissingServletRequestPartException.class })
    public Result<Void> handleMissingServletRequestPartException(
            MissingServletRequestPartException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 在Controller在接收的参数在进行绑定注解校验规则失败时将抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { BindException.class })
    public Result<Void> handleBindException(
            BindException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 找不到对应处理器时将抛出该异常
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public Result<Void> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(NOT_FOUND, ex));
    }

    // 异步处理超时时抛出该异常
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = { AsyncRequestTimeoutException.class })
    public Result<Void> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(SERVICE_UNAVAILABLE, ex));
    }

    // 在检验参数注解不通过时抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public Result<Void> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        warn(request, ex);
        Map<String, String> errors = ExceptionHandlerUtils.convertToErrors(ex);
        if (errors.isEmpty()) {
            return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
        } else {
            return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex), errors);
        }
    }

    // 文件上传异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MultipartException.class })
    public Result<Void> handleMultipartException(
            MultipartException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    // 异常码抛出捕获
    @ExceptionHandler(value = { ThrowableErrorCode.class })
    public ResponseEntity<Result<Void>> handleThrowableErrorCode(
            ThrowableErrorCode ex, HttpServletRequest request) {
        if (fun.fengwk.convention4j.api.code.HttpStatus.is4xx(ex)) {
            warn(request, ex);
        } else {
            error(request, ex);
        }
        HttpStatus httpStatus = HttpStatus.resolve(ex.getStatus());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(Results.error(ex), httpStatus);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public Result<Void> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { Throwable.class })
    public Result<Void> handleThrowable(
            Throwable ex, HttpServletRequest request) {
        error(request, ex);
        return Results.error(ExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
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
