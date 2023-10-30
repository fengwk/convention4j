package fun.fengwk.convention4j.common.cache.metrics;

import java.util.Set;

/**
 * @author fengwk
 */
public interface CacheManagerMetrics {

    /**
     * 调用缓存读方法。
     *
     * @param cacheName 缓存名称。
     * @param hitCache 是否命中缓存。
     */
    void read(String cacheName, boolean hitCache);

    /**
     * 获指定缓存取读方法调用次数。
     *
     * @param cacheName 缓存名称。
     * @return 读取方法调用次数。
     */
    long getReadCount(String cacheName);

    /**
     * 获取指定缓存读方法命中次数。
     *
     * @param cacheName 缓存名称。
     * @return 读方法命中次数。
     */
    long getReadHitCount(String cacheName);

    /**
     * 获取所有调用过读方法的缓存名称集合。
     *
     * @return 所有读方法的缓存名称集合。
     */
    Set<String> getAllReadCacheName();

    /**
     * 清理所有指标
     */
    void clear();

}
