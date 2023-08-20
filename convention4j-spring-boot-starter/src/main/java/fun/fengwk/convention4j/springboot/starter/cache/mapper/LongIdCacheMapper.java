package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.support.GsonLongIdCacheSupport;

/**
 * @author fengwk
 */
public interface LongIdCacheMapper<DO extends BaseCacheDO<Long>>
        // 继承顺序不能变，否则会导致default方法判断失效
        extends GsonLongIdCacheSupport<DO>, BaseCacheMapper<DO, Long> {
}
