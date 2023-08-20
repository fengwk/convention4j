package fun.fengwk.convention4j.springboot.starter.cache.support;

import java.util.Map;

/**
 * @author fengwk
 */
public interface SimpleIdCacheSupport<DATA, ID> extends CacheSupport<DATA, ID> {

    @Override
    default ID idKeyMapToId(Map<String, Object> idKeyMap) {
        if (idKeyMap == null || idKeyMap.isEmpty()) {
            return null;
        }
        return (ID) idKeyMap.values().iterator().next();
    }

}
