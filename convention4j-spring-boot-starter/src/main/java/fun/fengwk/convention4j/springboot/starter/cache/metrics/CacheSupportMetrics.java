package fun.fengwk.convention4j.springboot.starter.cache.metrics;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
public interface CacheSupportMetrics {

    /**
     * 调用了缓存仓库
     *
     * @param method 方法
     * @param partialCacheHit 部分缓存命中
     * @param fullCacheHit 全部缓存命中
     */
    void call(Method method, boolean partialCacheHit, boolean fullCacheHit);

    /**
     * 获取方法调用次数
     *
     * @param method 方法
     * @return 方法调用次数
     */
    long getCallCount(Method method);

    /**
     * 获取部分缓存命中次数
     *
     * @param method 方法
     * @return 部分缓存命中次数
     */
    long getPartialCacheHitCount(Method method);

    /**
     * 获取全部缓存命中次数
     *
     * @param method 方法
     * @return 全部缓存命中次数
     */
    long getFullCacheHitCount(Method method);

}
