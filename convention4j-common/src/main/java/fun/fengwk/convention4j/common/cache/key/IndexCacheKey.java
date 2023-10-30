package fun.fengwk.convention4j.common.cache.key;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class IndexCacheKey<ID> implements ZipKey {

    private final String prefix = "IC";
    private final IndexCacheKeyPrefix<ID> indexCacheKeyPrefix;
    private final String indexCacheVersion;

}
