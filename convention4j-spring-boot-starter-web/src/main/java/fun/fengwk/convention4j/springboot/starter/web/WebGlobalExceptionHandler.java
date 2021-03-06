package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.common.code.ErrorCodeFactory;
import fun.fengwk.convention4j.common.code.ThrowableErrorCode;
import fun.fengwk.convention4j.common.result.Result;
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

import static fun.fengwk.convention4j.common.code.CommonCodeTable.ILLEGAL_ARGUMENT;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.ILLEGAL_STATE;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.RESOURCE_NOT_FOUND;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.UNSUPPORTED_OPERATION;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.WAIT_TIMEOUT;

/**
 * REST??????????????????????????????
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @author fengwk
 */
@RestControllerAdvice
public class WebGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebGlobalExceptionHandler.class);

    private final ErrorCodeFactory errorCodeFactory;

    public WebGlobalExceptionHandler(ErrorCodeFactory errorCodeFactory) {
        this.errorCodeFactory = errorCodeFactory;
    }

    @PostConstruct
    public void init() {
        log.info("started {}", getClass().getSimpleName());
    }

    // ?????????HTTP????????????????????????
    @ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        warn(request, ex);

        // ??????????????????????????????????????????????????????
        HttpHeaders headers = new HttpHeaders();
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        return new ResponseEntity<>(Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, UNSUPPORTED_OPERATION, ex)),
                headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // ????????????????????????????????????????????????@RequestBody????????????application/json????????????????????????application/x-www-form-urlencoded?????????????????????
    @ExceptionHandler(value = { HttpMediaTypeNotSupportedException.class })
    public ResponseEntity<Result<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        warn(request, ex);

        // ?????????????????????????????????????????????????????????
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }

        return new ResponseEntity<>(Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, UNSUPPORTED_OPERATION, ex)),
                headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // ???????????????????????????????????????????????????????????????????????????????????????????????????
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = { HttpMediaTypeNotAcceptableException.class })
    public Result<Void> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, UNSUPPORTED_OPERATION, ex));
    }

    // ???url????????????@RequestMapping??????????????????????????????????????????????????????????????????
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { MissingPathVariableException.class })
    public Result<Void> handleMissingPathVariableException(
            MissingPathVariableException ex, HttpServletRequest request, WebRequest webRequest) {
        warn(request, ex);

        webRequest.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);

        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ?????????????????????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    public Result<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ??????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ServletRequestBindingException.class })
    public Result<Void> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ??????????????????
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { ConversionNotSupportedException.class })
    public Result<Void> handleConversionNotSupportedException(
            ConversionNotSupportedException ex, HttpServletRequest request) {
        error(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
    }

    // ??????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { TypeMismatchException.class })
    public Result<Void> handleTypeMismatchException(
            TypeMismatchException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ?????????????????????????????????????????????@RequestBody???required???????????????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { HttpMessageNotReadableException.class })
    public Result<Void> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ???????????????????????????
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { HttpMessageNotWritableException.class })
    public Result<Void> handleHttpMessageNotWritableException(
            HttpMessageNotWritableException ex, HttpServletRequest request) {
        error(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
    }

    // ?????????Controller?????????@Valid?????????Bean??????????????????????????????SpringMVC????????????????????????MethodArgumentNotValidException??????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public Result<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        warn(request, ex);
        Map<String, String> errors = convertToErrors(ex);
        if (errors.isEmpty()) {
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
        } else {
            return Results.of(errorCodeFactory.create(ILLEGAL_ARGUMENT), errors);
        }
    }

    // ??????????????????????????????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MissingServletRequestPartException.class })
    public Result<Void> handleMissingServletRequestPartException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ???Controller??????????????????????????????????????????????????????????????????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { BindException.class })
    public Result<Void> handleBindException(
            BindException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ?????????????????????????????????????????????
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public Result<Void> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, RESOURCE_NOT_FOUND, ex));
    }

    // ????????????????????????????????????
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = { AsyncRequestTimeoutException.class })
    public Result<Void> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
    }

    // ????????????????????????????????????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public Result<Void> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        warn(request, ex);
        Map<String, String> errors = ExceptionHandlerUtils.convertToErrors(ex);
        if (errors.isEmpty()) {
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
        } else {
            return Results.of(errorCodeFactory.create(ILLEGAL_ARGUMENT), errors);
        }
    }

    // ??????????????????
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MultipartException.class })
    public Result<Void> handleMultipartException(
            MultipartException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    // ?????????????????????
    @ExceptionHandler(value = { ThrowableErrorCode.class })
    public ResponseEntity<Result<Void>> handleThrowableErrorCode(
            ThrowableErrorCode ex, HttpServletRequest request) {
        HttpStatus status;
        if (ILLEGAL_STATE.equalsCode(ex)) {
            warn(request, ex);
            status = HttpStatus.BAD_REQUEST;
        } else if (WAIT_TIMEOUT.equalsCode(ex)) {
            error(request, ex);
            status = HttpStatus.GATEWAY_TIMEOUT;
        } else {
            // ??????????????????????????????????????????????????????error????????????????????????????????????????????????
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            errorUseShortFormat(request, ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(Results.of(ex), status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public Result<Void> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        warn(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { Throwable.class })
    public Result<Void> handleThrowable(
            Throwable ex, HttpServletRequest request) {
        error(request, ex);
        return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
    }

    private void warn(HttpServletRequest request, Throwable ex) {
        log.warn("request failed, request: {}, error: {}", formatRequest(request), String.valueOf(ex));
    }

    private void errorUseShortFormat(HttpServletRequest request, Throwable ex) {
        log.error("request failed, request: {}, error: {}", formatRequest(request), String.valueOf(ex));
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
