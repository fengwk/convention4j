package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static fun.fengwk.convention4j.api.code.CommonErrorCodes.*;

/**
 * @author fengwk
 */
@Slf4j
public class ResultExceptionHandlerUtils {

    private ResultExceptionHandlerUtils() {}

    public static <T> T getProxy(T target) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Method method = invocation.getMethod();
                if (method.getReturnType() == Result.class) {
                    try {
                        return invocation.proceed();
                    } catch (Throwable ex) {
                        return handleError(ex);
                    }
                } else {
                    return invocation.proceed();
                }
            }
        });
        return (T) proxyFactory.getProxy();
    }

    public static Result<?> handleError(Throwable ex) {
        if (ex instanceof ConstraintViolationException) {
            warn(ex);
            Map<String, String> errors = ResultExceptionHandlerUtils.convertToErrors((ConstraintViolationException) ex);
            ErrorCode errorCode = ResultExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex);
            if (errors.isEmpty()) {
                return Results.error(errorCode);
            } else {
                return Results.error(errorCode, errors);
            }
        } else if (ex instanceof ErrorCode) {
            ErrorCode exErrorCode = (ErrorCode) ex;
            if (HttpStatus.is4xx(exErrorCode)) {
                warn(ex);
            } else {
                errorUseShortFormat(ex);
            }
            return Results.error((ErrorCode) ex);
        } else if (ex instanceof IllegalArgumentException) {
            warn(ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
        }  else if (ex instanceof UnsupportedOperationException) {
            error(ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(NOT_IMPLEMENTED, ex));
        } else {
            error(ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
        }
    }

    private static void warn(Throwable ex) {
        log.warn("{} catch exception, error: {}", ResultExceptionHandler.class.getSimpleName(), String.valueOf(ex));
    }

    private static void errorUseShortFormat(Throwable ex) {
        log.error("{} catch exception, error: {}", ResultExceptionHandler.class.getSimpleName(), String.valueOf(ex));
    }

    private static void error(Throwable ex) {
        log.error("{} catch exception", ResultExceptionHandler.class.getSimpleName(), ex);
    }

    /**
     * 将异常转化为指定类型的错误码。
     *
     * @param conventionErrorCode
     * @param ex
     * @return
     */
    public static ErrorCode toErrorCode(ConventionErrorCode conventionErrorCode, Throwable ex) {
        String msg = ex.getLocalizedMessage();
        if (msg == null) {
            msg = ex.getMessage();
        }
        ErrorCode finalErrorCode;
        if (msg == null || msg.trim().isEmpty()) {
            finalErrorCode = conventionErrorCode;
        } else {
            finalErrorCode = conventionErrorCode.create(msg);
        }
        return finalErrorCode;
    }

    /**
     * 从ConstraintViolationException中提取错误信息映射。
     *
     * @param ex
     * @return
     */
    public static Map<String, String> convertToErrors(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();
        if (ex.getConstraintViolations() != null) {
            for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
                map.put(getProperty(cv.getPropertyPath()), cv.getMessage());
            }
        }
        return map;
    }

    private static String getProperty(Path path) {
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

}
