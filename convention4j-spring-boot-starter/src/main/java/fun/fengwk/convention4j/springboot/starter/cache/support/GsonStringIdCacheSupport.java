package fun.fengwk.convention4j.springboot.starter.cache.support;

/**
 * @author fengwk
 */
public interface GsonStringIdCacheSupport<DATA>
    extends GsonCacheSupport<DATA, String>, SimpleIdCacheSupport<DATA, String> {

    @Override
    default String serializeId(String id) {
        return id;
    }

    @Override
    default String deserializedId(String idCacheKey) {
        return idCacheKey;
    }

}
