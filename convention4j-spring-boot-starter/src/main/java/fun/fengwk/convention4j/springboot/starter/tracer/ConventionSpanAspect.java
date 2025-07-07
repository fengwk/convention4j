package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Slf4j
@Aspect
public class ConventionSpanAspect {

    @Around("@annotation(fun.fengwk.convention4j.springboot.starter.tracer.ConventionSpan)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        Method annotatedMethod = findMethod(joinPoint);
        if (annotatedMethod == null) {
            log.error("Can not found @ConventionSpan method, signature: {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        }

        MergedAnnotation<ConventionSpan> conventionSpanAnnotation = MergedAnnotations
            .from(annotatedMethod, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ConventionSpan.class);
        if (!conventionSpanAnnotation.isPresent()) {
            log.error("Can not found @ConventionSpan, signature: {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        }

        ConventionSpan conventionSpan = conventionSpanAnnotation.synthesize();
        String operationName = conventionSpan.value();
        if (StringUtils.isBlank(operationName)) {
            operationName = buildOperationName(annotatedMethod);
        }

        SpanInfo spanInfo = SpanInfo.builder()
            .operationName(operationName)
            .alias(conventionSpan.alias())
            .kind(conventionSpan.kind())
            .propagation(conventionSpan.propagation())
            .build();
        return TracerUtils.executeAndReturn(joinPoint::proceed, spanInfo);
    }

    private Method findMethod(ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target == null) {
            return null;
        }

        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature methodSignature)) {
            return null;
        }

        Class<?> targetClass = AopUtils.getTargetClass(target);
        try {
            return targetClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            log.error("Find @ConventionSpan method error", ex);
            return null;
        }
    }

    private static String buildOperationName(Method method) {
        if (method != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            StringBuilder sb = new StringBuilder(
                method.getDeclaringClass().getSimpleName());
            sb.append('#').append(method.getName()).append('(');
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(parameterTypes[i].getSimpleName());
            }
            sb.append(')');
            return sb.toString();

        }

        return "";
    }

}
