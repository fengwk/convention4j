package fun.fengwk.convention4j.common.cache.key;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class CacheKeyPrefix {

    private final String prefix = "fun.fengwk.convention4j.common.cache.CacheKeyPrefix";
    private final String cacheManagerVersion;
    private final String cacheName;
    private final String cacheVersion;

}
