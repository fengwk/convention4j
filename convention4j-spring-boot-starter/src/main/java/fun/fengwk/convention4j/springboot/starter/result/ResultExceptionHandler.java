package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常结果处理，如果方法的返回类型是{@link Result}，那么将会把异常处理为Result结果。
 *
 * @author fengwk
 */
@Slf4j
@Aspect
public class ResultExceptionHandler {

    // 拦截所有返回Result及其子类的方法
    @Around("@within(fun.fengwk.convention4j.springboot.starter.result.AutoResultExceptionHandler) " +
            "&& execution(fun.fengwk.convention4j.api.result.Result+ *.*(..))")
    public Result<?> handle(ProceedingJoinPoint joinPoint) {
        try {
            return (Result<?>) joinPoint.proceed();
        } catch (Throwable err) {
            return ResultExceptionHandlerUtils.handleError(err, log);
        }
    }

}
