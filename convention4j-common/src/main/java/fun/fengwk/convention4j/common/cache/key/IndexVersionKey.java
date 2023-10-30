package fun.fengwk.convention4j.common.cache.key;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class IndexVersionKey<ID> implements ZipKey {

    private final String prefix = "IV";
    private final IndexCacheKeyPrefix<ID> indexCacheKeyPrefix;

}
