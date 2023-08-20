package fun.fengwk.convention4j.springboot.starter.cache.adapter;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
public class StringRedisTemplateCacheAdapter implements CacheAdapter {

    private final StringRedisTemplate redisTemplate;

    public StringRedisTemplateCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, int expireSeconds) {
        if (key == null || expireSeconds <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void batchSet(Map<String, String> kvMap, int expireSeconds) {
        if (kvMap == null || kvMap.isEmpty() || expireSeconds <= 0) {
            return;
        }
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (Map.Entry<String, String> entry : kvMap.entrySet()) {
                    operations.opsForValue().set((K) entry.getKey(), (V) entry.getValue(), expireSeconds, TimeUnit.SECONDS);
                }
                return null;
            }
        });
    }

    @Override
    public Map<String, String> batchGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        assert values != null; // non pipeline
        Map<String, String> kvMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                kvMap.put(keys.get(i), value);
            }
        }
        return kvMap;
    }

    @Override
    public void batchDelete(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }

}
