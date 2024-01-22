package fun.fengwk.convention4j.oauth2.sdk.aspect;

import fun.fengwk.convention4j.oauth2.sdk.context.OAuth2Context;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Aspect
public class AuthorizationAspect<SUBJECT> {

    private final OAuth2Context<SUBJECT> oauth2Context;

    @Around("@annotation(fun.fengwk.convention4j.oauth2.sdk.annotation.Authorization)" +
        "|| @within(fun.fengwk.convention4j.oauth2.sdk.annotation.Authorization)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        oauth2Context.getSubjectRequired();
        return joinPoint.proceed();
    }

}
