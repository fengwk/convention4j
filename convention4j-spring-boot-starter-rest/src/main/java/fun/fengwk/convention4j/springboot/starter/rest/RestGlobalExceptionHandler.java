package fun.fengwk.convention4j.springboot.starter.rest;

import com.google.common.collect.ImmutableMap;
import fun.fengwk.convention4j.api.code.CodeTable;
import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeFactory;
import fun.fengwk.convention4j.api.code.ThrowableErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.api.result.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static fun.fengwk.convention4j.api.code.CommonCodeTable.*;

/**
 * rest协议的异常处理程序。
 * 
 * @author fengwk
 */
@RestControllerAdvice
public class RestGlobalExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestGlobalExceptionHandler.class);

    private final ErrorCodeFactory codeFactory;
    
    public RestGlobalExceptionHandler(ErrorCodeFactory codeFactory) {
        this.codeFactory = codeFactory;
    }
    
    @PostConstruct
    public void init() {
        LOG.info("Started {}", getClass().getSimpleName());
    }

    // 找不到对应处理器时将抛出该异常
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public Result<Void> handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_RESOURCE_NOT_FOUND, e));
    }
    
    // 在检验参数注解不通过时抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public Result<Void> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        warn(request, e);
        ImmutableMap<String, String> errors = convertToErrors(e);
        if (errors.isEmpty()) {
            return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
        } else {
            return Results.of(codeFactory.create(A_ILLEGAL_ARGUMENT, tryGetFirstError(errors)), errors);
        }
    }
    
    // 在校验@Valid注解对象内部发生错误时抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public Result<Void> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        warn(request, e);
        ImmutableMap<String, String> errors = convertToErrors(e);
        if (errors.isEmpty()) {
            return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
        } else {
            return Results.of(codeFactory.create(A_ILLEGAL_ARGUMENT, tryGetFirstError(errors)), errors);
        }
    }
    
    // 在Controller在接收的参数在进行绑定注解校验规则失败时将抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { BindException.class })
    public Result<Void> handleBindException(HttpServletRequest request, BindException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }
    
    // 缺少请求入参产生的异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    public Result<Void> handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }

    // 当传入参数与方法参数类型不匹配时抛出该异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
    public Result<Void> handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }
    
    // 文件上传异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { MultipartException.class })
    public Result<Void> handleMultipartException(HttpServletRequest request, MultipartException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }
    
    // 使用了不支持的类型
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { HttpMediaTypeNotSupportedException.class })
    public Result<Void> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }
    
    // 不支持传入的HTTP请求方法
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class })
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }

    // 参数绑定异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { ServletRequestBindingException.class })
    public Result<Void> handleServletRequestBindingException(HttpServletRequest request, ServletRequestBindingException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }

    // 异常码抛出捕获
    @ExceptionHandler(value = { ThrowableErrorCode.class })
    public ResponseEntity<Result<Void>> handleThrowableErrorCode(HttpServletRequest request, ThrowableErrorCode e) {
        HttpStatus status;
        if (e.sourceOf(ErrorCode.SOURCE_A)) {
            warn(request, e);
            status = HttpStatus.BAD_REQUEST;
        } else {
            error(request, e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(Results.of(e), status);
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public Result<Void> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        warn(request, e);
        return Results.of(toErrorCode(A_ILLEGAL_ARGUMENT, e));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = { Throwable.class })
    public Result<Void> handleThrowable(HttpServletRequest request, Throwable e) {
        error(request, e);
        return Results.of(toErrorCode(B_ILLEGAL_STATE, e));
    }
    
    private void warn(HttpServletRequest request, Throwable e) {
        LOG.warn("Request failed, request: [{}], error: [{}]", formatRequest(request), String.valueOf(e));
    }
    
    private void error(HttpServletRequest request, Throwable e) {
        LOG.error("Request failed, request: [{}]", formatRequest(request), e);
    }
    
    private String formatRequest(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI() 
                + (request.getQueryString() == null || request.getQueryString().isEmpty() ? "" : "?" + request.getQueryString());
    }

    private ErrorCode toErrorCode(CodeTable errCode, Throwable e) {
        String msg = e.getLocalizedMessage();
        ErrorCode errorCode;
        if (msg == null || msg.trim().isEmpty()) {
            errorCode = codeFactory.create(errCode);
        } else {
            errorCode = codeFactory.create(errCode, msg);
        }
        return errorCode;
    }
    
    private ImmutableMap<String, String> convertToErrors(ConstraintViolationException e) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Map<String, String> errors = new HashMap<>();
        if (e.getConstraintViolations() != null) {
            for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
                builder.put(getProperty(cv.getPropertyPath()), cv.getMessage());
            }
        }
        return builder.build();
    }
    
    private ImmutableMap<String, String> convertToErrors(MethodArgumentNotValidException e) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            builder.put(fe.getObjectName() + "." + fe.getField(), fe.getDefaultMessage());
        }
        return builder.build();
    }
    
    private String getProperty(Path path) {
        StringBuilder property = new StringBuilder();
        for (Path.Node node : path) {
            ElementKind kind = node.getKind();
            if (kind == ElementKind.PARAMETER || kind == ElementKind.PROPERTY || kind == ElementKind.RETURN_VALUE) {
                if (property.length() > 0) {
                    property.append('.');
                }
                property.append(node.getName());
            }
        }
        return property.length() > 0 ? property.toString() : path.toString();
    }
    
    private String tryGetFirstError(Map<String, String> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        
        Entry<String, String> first = errors.entrySet().iterator().next();
        return first.getKey() + ": " + first.getValue();
    }

}
