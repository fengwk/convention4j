package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.springboot.starter.cache.meta.CacheReadMethodMeta;
import fun.fengwk.convention4j.springboot.starter.cache.meta.CacheSupportMeta;
import fun.fengwk.convention4j.springboot.starter.cache.meta.CacheWriteMethodMeta;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
public class CacheSupportMethodInterceptor implements MethodInterceptor {

    private final CacheSupportMethodHandler cacheSupportMethodHandler;
    private final CacheSupportMetaManager supportMetaManager;

    public CacheSupportMethodInterceptor(
            CacheSupportMethodHandler cacheSupportMethodHandler, CacheSupportMetaManager supportMetaManager) {
        this.cacheSupportMethodHandler = cacheSupportMethodHandler;
        this.supportMetaManager = supportMetaManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 获取仓库元信息
        CacheSupportMeta supportMeta = supportMetaManager.getSupportMeta(invocation.getThis());
        // 如果没有仓库元信息，则不走缓存
        if (supportMeta == null) {
            return invocation.proceed();
        }

        // 决定走缓存还是不走缓存
        Method method = invocation.getMethod();
        CacheWriteMethodMeta writeMethodMeta = supportMeta.getWriteMethodMeta(method);
        if (writeMethodMeta != null) {
            // 清理缓存
            return cacheSupportMethodHandler.handleCacheWriteMethod(invocation, supportMeta, writeMethodMeta);
        } else {
            CacheReadMethodMeta readMethodMeta = supportMeta.getReadMethodMeta(method);
            if (readMethodMeta != null) {
                // 读取缓存
                return cacheSupportMethodHandler.handleCacheReadMethod(invocation, supportMeta, readMethodMeta);
            } else {
                return invocation.proceed();
            }
        }
    }

}
