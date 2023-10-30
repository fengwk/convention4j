package fun.fengwk.convention4j.common.cache.key;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class IndexCacheKeyPrefix<I> {

    private final String prefix = "fun.fengwk.convention4j.common.cache.IndexCacheKeyPrefix";
    private final String cacheManagerVersion;
    private final I index;

}
