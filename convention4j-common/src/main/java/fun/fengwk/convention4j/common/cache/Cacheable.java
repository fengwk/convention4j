package fun.fengwk.convention4j.common.cache;

import java.util.List;
import java.util.TreeMap;

/**
 * @author fengwk
 */
public interface Cacheable<O> {

    /**
     * 获取缓存名称。
     * @return 缓存名称。
     */
    String getCacheName();

    /**
     * 获取缓存版本。
     * @return 缓存版本。
     */
    String getCacheVersion();

    /**
     * 从参数表中获取listenKeyGroup列表。
     * @param params 参数表。
     * @return listenKeyGroup列表。
     */
    List<TreeMap<String, Object>> extractListenKeyGroupListFromParams(Object[] params);

    /**
     * 从数据值中获取listenKeyGroup。
     * @param obj 要缓存的对象。
     * @return listenKeyGroup。
     */
    List<TreeMap<String, Object>> extractListenKeyGroupListFromObj(O obj);

}
