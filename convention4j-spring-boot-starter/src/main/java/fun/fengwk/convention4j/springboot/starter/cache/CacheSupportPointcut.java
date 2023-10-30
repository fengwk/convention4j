package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheSupport;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.ReadMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.WriteMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.meta.CacheAnnotationMetaReader;
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

    @Override
    public ClassFilter getClassFilter() {
        return clazz -> AnnotationUtils.findAnnotation(clazz, CacheSupport.class) != null;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return true;
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

}
