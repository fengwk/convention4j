package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Slf4j
@Aspect
public class ReactorConventionSpanAspect extends ConventionSpanAspect {

    @Override
    protected Object doHandle(ProceedingJoinPoint joinPoint, Method annotatedMethod, SpanInfo spanInfo) throws Throwable {
        Class returnType = annotatedMethod.getReturnType();
        return ReactorTracerUtils.executeAndReturn(joinPoint::proceed, spanInfo, returnType);
    }

}
