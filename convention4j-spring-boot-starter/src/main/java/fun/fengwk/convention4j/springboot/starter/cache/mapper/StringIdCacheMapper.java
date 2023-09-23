package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.support.GsonStringIdCacheSupport;

/**
 * @author fengwk
 */
public interface StringIdCacheMapper<PO extends BaseCachePO<String>>
        // 继承顺序不能变，否则会导致default方法判断失效
        extends GsonStringIdCacheSupport<PO>, BaseCacheMapper<PO, String> {
}
