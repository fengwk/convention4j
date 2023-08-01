package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import java.util.Map;

import static fun.fengwk.convention4j.common.code.ErrorCodes.BAD_REQUEST;
import static fun.fengwk.convention4j.common.code.ErrorCodes.INTERNAL_SERVER_ERROR;
import static fun.fengwk.convention4j.common.code.ErrorCodes.NOT_IMPLEMENTED;

/**
 * 异常结果处理，如果方法的返回类型是{@link Result}，那么将会把异常处理为Result结果。
 *
 * @author fengwk
 */
@Aspect
public class ResultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResultExceptionHandler.class);

    // 拦截所有返回Result及其子类的方法
    @Around("@within(fun.fengwk.convention4j.springboot.starter.result.AutoResultExceptionHandler) " +
            "&& execution(fun.fengwk.convention4j.api.result.Result+ *.*(..))")
    public Result<?> handle(ProceedingJoinPoint joinPoint) {
        try {
            return (Result<?>) joinPoint.proceed();
        } catch (Throwable err) {
            return handleError(err);
        }
    }

    private Result<?> handleError(Throwable ex) {
        if (ex instanceof ConstraintViolationException) {
            warn(ex);
            Map<String, String> errors = ExceptionHandlerUtils.convertToErrors((ConstraintViolationException) ex);
            ErrorCode errorCode = ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex);
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
            return Results.error(ExceptionHandlerUtils.toErrorCode(BAD_REQUEST, ex));
        }  else if (ex instanceof UnsupportedOperationException) {
            error(ex);
            return Results.error(ExceptionHandlerUtils.toErrorCode(NOT_IMPLEMENTED, ex));
        } else {
            error(ex);
            return Results.error(ExceptionHandlerUtils.toErrorCode(INTERNAL_SERVER_ERROR, ex));
        }
    }

    private void warn(Throwable ex) {
        log.warn("{} catch exception, error: {}", ResultExceptionHandler.class.getSimpleName(), String.valueOf(ex));
    }

    private void errorUseShortFormat(Throwable ex) {
        log.error("{} catch exception, error: {}", ResultExceptionHandler.class.getSimpleName(), String.valueOf(ex));
    }

    private void error(Throwable ex) {
        log.error("{} catch exception", ResultExceptionHandler.class.getSimpleName(), ex);
    }

}
