package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeFactory;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.api.result.Results;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import java.util.Map;

import static fun.fengwk.convention4j.api.code.CommonCodeTable.ILLEGAL_ARGUMENT;
import static fun.fengwk.convention4j.api.code.CommonCodeTable.ILLEGAL_STATE;
import static fun.fengwk.convention4j.api.code.CommonCodeTable.UNSUPPORTED_OPERATION;

/**
 * 异常结果处理，如果方法的返回类型是{@link Result}，那么将会把异常处理为Result结果。
 *
 * @author fengwk
 */
@Aspect
public class ResultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResultExceptionHandler.class);

    private final ErrorCodeFactory errorCodeFactory;

    public ResultExceptionHandler(ErrorCodeFactory errorCodeFactory) {
        this.errorCodeFactory = errorCodeFactory;
    }

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
            if (errors.isEmpty()) {
                return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
            } else {
                return Results.of(errorCodeFactory.create(ILLEGAL_ARGUMENT), errors);
            }
        } else if (ex instanceof ErrorCode) {
            if (ILLEGAL_STATE.equalsCode((ErrorCode) ex)) {
                warn(ex);
            } else {
                errorUseShortFormat(ex);
            }
            return Results.of((ErrorCode) ex);
        } else if (ex instanceof IllegalArgumentException) {
            warn(ex);
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_ARGUMENT, ex));
        } else if (ex instanceof IllegalStateException) {
            error(ex);
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
        } else if (ex instanceof UnsupportedOperationException) {
            warn(ex);
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, UNSUPPORTED_OPERATION, ex));
        } else {
            error(ex);
            return Results.of(ExceptionHandlerUtils.toErrorCode(errorCodeFactory, ILLEGAL_STATE, ex));
        }
    }

    private void warn(Throwable ex) {
        log.warn("ResultExceptionHandler catch exception, error={}", String.valueOf(ex));
    }

    private void errorUseShortFormat(Throwable ex) {
        log.error("ResultExceptionHandler catch exception, error={}", String.valueOf(ex));
    }

    private void error(Throwable ex) {
        log.error("ResultExceptionHandler catch exception", ex);
    }

}
