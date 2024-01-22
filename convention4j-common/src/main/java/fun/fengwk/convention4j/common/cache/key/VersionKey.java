package fun.fengwk.convention4j.common.cache.key;

import lombok.Data;

import java.util.TreeMap;

/**
 * @author fengwk
 */
@Data
public class VersionKey implements ZipKey {

    private final String prefix = getClass().getName();
    private final CacheKeyPrefix cacheKeyPrefix;
    private final TreeMap<String, Object> listenKeyGroup;

    public VersionKey(CacheKeyPrefix cacheKeyPrefix, TreeMap<String, Object> listenKeyGroup) {
        this.cacheKeyPrefix = cacheKeyPrefix;
        this.listenKeyGroup = KeyUtils.adaptKeyable(listenKeyGroup);
    }

}
