package fun.fengwk.convention4j.common.result;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static fun.fengwk.convention4j.api.code.CommonErrorCodes.*;

/**
 * @author fengwk
 */
public class ResultExceptionHandlerUtils {

    private ResultExceptionHandlerUtils() {}

    public static <T> T getProxy(T target, Logger log) {
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
                        return handleError(ex, log);
                    }
                } else {
                    return invocation.proceed();
                }
            }
        });
        return (T) proxyFactory.getProxy();
    }

    public static Result<?> handleError(Throwable ex, Logger log) {
        if (ex instanceof ConstraintViolationException) {
            warn(log, ex);
            Map<String, String> errors = ResultExceptionHandlerUtils.convertToErrors((ConstraintViolationException) ex);
            ConventionErrorCode errorCode = ResultExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex);
            if (errors.isEmpty()) {
                return Results.error(errorCode);
            } else {
                return Results.error(errorCode, errors);
            }
        } else if (ex instanceof ConventionErrorCode errorCode) {
            if (HttpStatus.is4xx(errorCode.getStatus())) {
                warn(log, errorCode);
            } else {
                errorUseShortFormat(log, errorCode);
            }
            return Results.error(errorCode);
        } else if (ex instanceof IllegalArgumentException) {
            warn(log, ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
        }  else if (ex instanceof UnsupportedOperationException) {
            error(log, ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(NOT_IMPLEMENTED, ex));
        } else {
            error(log, ex);
            return Results.error(ResultExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
        }
    }

    private static void warn(Logger log, ConventionErrorCode errorCode) {
        if (log != null) {
            log.warn("Catch exception, code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
        }
    }

    private static void errorUseShortFormat(Logger log, ConventionErrorCode errorCode) {
        if (log != null) {
            log.error("Catch exception, code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
        }
    }

    private static void warn(Logger log, Throwable ex) {
        if (log != null) {
            log.warn("Catch exception, error: {}", String.valueOf(ex));
        }
    }

    private static void error(Logger log, Throwable ex) {
        if (log != null) {
            log.error("Catch exception", ex);
        }
    }

    /**
     * 将异常转化为指定类型的错误码。
     *
     * @param errorCode
     * @param ex
     * @return
     */
    public static ConventionErrorCode toErrorCode(ConventionErrorCode errorCode, Throwable ex) {
        String msg = ex.getLocalizedMessage();
        if (msg == null) {
            msg = ex.getMessage();
        }
        ConventionErrorCode finalErrorCode;
        if (msg == null || msg.trim().isEmpty()) {
            finalErrorCode = errorCode;
        } else {
            finalErrorCode = errorCode.resolve(msg);
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
                if (!property.isEmpty()) {
                    property.append('.');
                }
                property.append(node.getName());
            }
        }
        return !property.isEmpty() ? property.toString() : path.toString();
    }

}
