package fun.fengwk.convention4j.common.cache;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
public class MemoryCacheFacade implements CacheFacade {

    private final ConcurrentMap<String, CacheObject> cache = new ConcurrentHashMap<>();

    @Override
    public void set(String key, String value, int expireSeconds) {
        cache.put(key, new CacheObject(value, System.currentTimeMillis(), expireSeconds * 1000L));
    }

    @Override
    public String get(String key) {
        CacheObject cacheObject = cache.get(key);
        return cacheObject != null && !cacheObject.isExpired() ? cacheObject.getValue() : null;
    }

    @Override
    public void batchSet(Map<String, String> kvMap, int expireSeconds) {
        for (Map.Entry<String, String> entry : kvMap.entrySet()) {
            set(entry.getKey(), entry.getValue(), expireSeconds);
        }
    }

    @Override
    public Map<String, String> batchGet(Collection<String> keys) {
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            String value = get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public void batchDelete(Collection<String> keys) {
        for (String key : keys) {
            cache.remove(key);
        }
    }

    static class CacheObject {

        private String value;
        private long setTime;
        private long expireTime;

        public CacheObject(String value, long setTime, long expireTime) {
            this.value = value;
            this.setTime = setTime;
            this.expireTime = expireTime;
        }

        public String getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > setTime + expireTime;
        }

    }

}
