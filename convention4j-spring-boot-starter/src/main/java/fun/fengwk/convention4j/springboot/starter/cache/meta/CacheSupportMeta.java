package fun.fengwk.convention4j.springboot.starter.cache.meta;

import fun.fengwk.convention4j.springboot.starter.cache.exception.CacheParseException;
import fun.fengwk.convention4j.springboot.starter.cache.support.CacheSupport;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Data
public class CacheSupportMeta {

    private final CacheSupport<?, ?> support;
    private final Class<?> dataClass;
    private final List<KeyMeta> dataKeyMetas;
    private final List<CacheReadMethodMeta> cacheReadMethodMetas = new ArrayList<>();
    private final List<CacheWriteMethodMeta> cacheWriteMethodMetas = new ArrayList<>();

    public CacheSupportMeta(
            CacheSupport<?, ?> support, Class<?> dataClass, List<KeyMeta> dataKeyMetas) {
        this.support = Objects.requireNonNull(support);
        this.dataClass = Objects.requireNonNull(dataClass);
        this.dataKeyMetas = Objects.requireNonNull(dataKeyMetas);
    }

    public void check() {
        for (CacheReadMethodMeta cacheReadMethodMeta : cacheReadMethodMetas) {
            Map<String, KeyMeta> toDataKeyMetaMap = new HashMap<>();
            for (KeyMeta readMethodKeyMeta : cacheReadMethodMeta.getCacheKeyMetas()) {
                String name = readMethodKeyMeta.getName();
                KeyMeta dataKeyMeta = getDataKeyMetaByName(name);
                if (dataKeyMeta != null) {
                    toDataKeyMetaMap.put(name, dataKeyMeta);
                } else {
                    throw new CacheParseException(
                        "Cache read method '" + cacheReadMethodMeta.getMethod() + "' key '" + name + "' must be contained in data key");
                }
            }
            cacheReadMethodMeta.setToDataKeyMetaMap(toDataKeyMetaMap);
        }

        for (CacheWriteMethodMeta cacheWriteMethodMeta : cacheWriteMethodMetas) {
            for (KeyMeta readMethodKeyMeta : cacheWriteMethodMeta.getCacheKeyMetas()) {
                String name = readMethodKeyMeta.getName();
                if (getDataKeyMetaByName(name) == null) {
                    throw new CacheParseException(
                        "Cache write method key '" + name + "' must be contained in data key");
                }
            }
        }
    }

    private KeyMeta getDataKeyMetaByName(String name) {
        for (KeyMeta dataKeyMeta : dataKeyMetas) {
            if (dataKeyMeta.getName().equals(name)) {
                return dataKeyMeta;
            }
        }
        return null;
    }

    public CacheReadMethodMeta getReadMethodMeta(Method method) {
        for (CacheReadMethodMeta cacheMethodMeta : cacheReadMethodMetas) {
            if (Objects.equals(cacheMethodMeta.getMethod(), method)) {
                return cacheMethodMeta;
            }
        }
        return null;
    }

    public CacheWriteMethodMeta getWriteMethodMeta(Method method) {
        for (CacheWriteMethodMeta cacheMethodMeta : cacheWriteMethodMetas) {
            if (Objects.equals(cacheMethodMeta.getMethod(), method)) {
                return cacheMethodMeta;
            }
        }
        return null;
    }

    public void addCacheReadMethodMeta(CacheReadMethodMeta cacheReadMethodMeta) {
        cacheReadMethodMetas.add(cacheReadMethodMeta);
    }

    public void addCacheWriteMethodMeta(CacheWriteMethodMeta cacheWriteMethodMeta) {
        cacheWriteMethodMetas.add(cacheWriteMethodMeta);
    }


    public Map<String, Object> buildIdKeyMapByData(Object data) {
        return KeyMeta.buildKeyMap(dataKeyMetas.stream()
                .filter(KeyMeta::isId).collect(Collectors.toList()), k -> data, KeyMeta::getValue);
    }

}
