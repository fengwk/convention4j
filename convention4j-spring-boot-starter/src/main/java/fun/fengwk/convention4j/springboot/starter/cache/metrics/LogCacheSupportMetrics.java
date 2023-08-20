package fun.fengwk.convention4j.springboot.starter.cache.metrics;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
public class LogCacheSupportMetrics implements CacheSupportMetrics {

    private final ConcurrentMap<Method, Long> callCounterMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Method, Long> partialCacheHitCounterMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Method, Long> fullCacheHitCounterMap = new ConcurrentHashMap<>();

    @Override
    public void call(Method method, boolean partialCacheHit, boolean fullCacheHit) {
        long callCount, partialCacheHitCount = 0, fullCacheHitCount = 0;
        callCount = callCounterMap.compute(method, (k, v) -> v == null ? 1 : v + 1);
        if (partialCacheHit) {
            partialCacheHitCount = partialCacheHitCounterMap.compute(method, (k, v) -> v == null ? 1 : v + 1);
        } else if (log.isDebugEnabled()) {
            partialCacheHitCount = getPartialCacheHitCount(method);
        }
        if (fullCacheHit) {
            fullCacheHitCount = fullCacheHitCounterMap.compute(method, (k, v) -> v == null ? 1 : v + 1);
        } else if (log.isDebugEnabled()) {
            fullCacheHitCount = getFullCacheHitCount(method);
        }
        log.debug("{} cache, callCount: {}, partialCacheHitCount: {}, fullHitCount: {}, method: {}",
            (fullCacheHit ? "full" : (partialCacheHit ? "partial" : "none")),
            callCount, partialCacheHitCount, fullCacheHitCount, method);
    }

    @Override
    public long getCallCount(Method method) {
        return callCounterMap.getOrDefault(method, 0L);
    }

    @Override
    public long getPartialCacheHitCount(Method method) {
        return partialCacheHitCounterMap.getOrDefault(method, 0L);
    }

    @Override
    public long getFullCacheHitCount(Method method) {
        return fullCacheHitCounterMap.getOrDefault(method, 0L);
    }

}
