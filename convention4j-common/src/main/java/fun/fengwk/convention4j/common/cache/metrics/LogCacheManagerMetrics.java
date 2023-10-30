package fun.fengwk.convention4j.common.cache.metrics;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author fengwk
 */
@Slf4j
public class LogCacheManagerMetrics implements CacheManagerMetrics {

    private final ConcurrentMap<String, Adder> cacheName2AdderMap = new ConcurrentHashMap<>();

    @Override
    public void read(String cacheName, boolean hitCache) {
        Adder adder = cacheName2AdderMap.computeIfAbsent(cacheName, k -> new Adder());
        adder.read(hitCache);
    }

    @Override
    public long getReadCount(String cacheName) {
        Adder adder = cacheName2AdderMap.get(cacheName);
        return adder == null ? 0 : adder.countAdder.sum();
    }

    @Override
    public long getReadHitCount(String cacheName) {
        Adder adder = cacheName2AdderMap.get(cacheName);
        return adder == null ? 0 : adder.hitAdder.sum();
    }

    @Override
    public Set<String> getAllReadCacheName() {
        return Collections.unmodifiableSet(cacheName2AdderMap.keySet());
    }

    @Override
    public void clear() {
        cacheName2AdderMap.clear();
    }

    static class Adder {

        private final LongAdder countAdder = new LongAdder();
        private final LongAdder hitAdder = new LongAdder();

        void read(boolean hit) {
            countAdder.increment();
            if (hit) {
                hitAdder.increment();
            }
        }

    }

}
