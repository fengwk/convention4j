package fun.fengwk.convention4j.springboot.starter.cache.support;

/**
 * @author fengwk
 */
public interface GsonLongIdCacheSupport<DATA>
    extends GsonCacheSupport<DATA, Long>, SimpleIdCacheSupport<DATA, Long> {

    @Override
    default String serializeId(Long id) {
        return id == null ? null : String.valueOf(id);
    }

    @Override
    default Long deserializedId(String idCacheKey) {
        return idCacheKey == null ? null : Long.valueOf(idCacheKey);
    }

}
