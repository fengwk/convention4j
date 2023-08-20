package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheReadMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheWriteMethod;
import fun.fengwk.convention4j.springboot.starter.cache.support.*;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fengwk
 */
public class CacheSupportPointcut implements Pointcut {

    private static final Set<Class<?>> IGNORE_CLASSES = new HashSet<>(Arrays.asList(
        Object.class, CacheSupport.class, SimpleIdCacheSupport.class,
        GsonCacheSupport.class, GsonLongIdCacheSupport.class, GsonStringIdCacheSupport.class
    ));

    @Override
    public ClassFilter getClassFilter() {
        return CacheSupport.class::isAssignableFrom;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                if (skipMethod(method)) {
                    return false;
                }
                return AnnotationUtils.findAnnotation(method, CacheReadMethod.class) != null
                    || AnnotationUtils.findAnnotation(method, CacheWriteMethod.class) != null;
            }

            @Override
            public boolean isRuntime() {
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private boolean skipMethod(Method method) {
        for (Class<?> clazz : IGNORE_CLASSES) {
            if (ClassUtils.hasMethod(clazz, method.getName(), method.getParameterTypes())) {
                return true;
            }
        }
        return false;
    }

}
