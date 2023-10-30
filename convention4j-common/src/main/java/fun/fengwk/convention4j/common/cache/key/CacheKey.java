package fun.fengwk.convention4j.common.cache.key;

import fun.fengwk.convention4j.common.NullSafe;
import lombok.Data;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author fengwk
 */
@Data
public class CacheKey implements ZipKey {

    private final String prefix = "C";
    private final CacheKeyPrefix cacheKeyPrefix;
    private final TreeSet<String> sortedVersionKey;
    private final Object[] params;

    public CacheKey(CacheKeyPrefix cacheKeyPrefix, Collection<String> versionKeys, Object[] params) {
        this.cacheKeyPrefix = cacheKeyPrefix;
        this.sortedVersionKey = versionKeys instanceof TreeSet ?
            (TreeSet<String>) versionKeys : new TreeSet<>(NullSafe.of(versionKeys));
        this.params = KeyUtils.adaptKeyable(params);
    }

}
