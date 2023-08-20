package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheReadMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.IdKey;
import fun.fengwk.convention4j.springboot.starter.cache.support.CacheSupport;
import fun.fengwk.convention4j.springboot.starter.mapper.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
public interface BaseCacheMapper<DO extends BaseCacheDO<ID>, ID> extends BaseMapper, CacheSupport<DO, ID> {

    @CacheReadMethod(useIdQuery = true)
    List<DO> findByIdIn(@IdKey("id") Collection<ID> ids);

    @Override
    default List<DO> doListByIds(Collection<ID> ids) {
        return findByIdIn(ids);
    }

}
