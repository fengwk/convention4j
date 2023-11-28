package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.persistence.BaseDO;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
 * 可缓存的Mapper。
 *
 * @author fengwk
 */
@MapperCacheSupport
public interface CacheableMapper<DO extends BaseDO<ID>, ID> extends BaseMapper {

    /**
     * 通过id集合批量查询。
     *
     * @param ids id集合。
     * @return PO集合。
     */
    List<DO> findForUpdateByIdIn(Collection<ID> ids);

}
