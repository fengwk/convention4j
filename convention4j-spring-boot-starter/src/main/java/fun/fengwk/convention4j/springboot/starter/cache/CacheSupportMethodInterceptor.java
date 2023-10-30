package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.common.cache.exception.CacheExecuteException;
import fun.fengwk.convention4j.springboot.starter.cache.registry.DefaultCacheManager;
import fun.fengwk.convention4j.springboot.starter.cache.registry.DefaultCacheManagerRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
public class CacheSupportMethodInterceptor implements MethodInterceptor {

    private final DefaultCacheManagerRegistry defaultCacheManagerRegistry;

    public CacheSupportMethodInterceptor(DefaultCacheManagerRegistry indexCacheManagerRegistry) {
        this.defaultCacheManagerRegistry = indexCacheManagerRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        DefaultCacheManager<?> cacheManager = defaultCacheManagerRegistry.getCacheManager(invocation.getThis());
        if (cacheManager != null) {
            Method method = invocation.getMethod();
            if (cacheManager.isReadMethod(method)) {
                return cacheManager.read(method, params -> doInvoke(invocation),
                    invocation.getArguments(), method.getGenericReturnType());
            } else if (cacheManager.isWriteMethod(method)) {
                return cacheManager.write(method, params -> doInvoke(invocation),
                    invocation.getArguments());
            }
        }
        return invocation.proceed();
    }

    private Object doInvoke(MethodInvocation invocation) {
        try {
            return invocation.proceed();
        } catch (Throwable err) {
            throw new CacheExecuteException(err);
        }
    }

}
