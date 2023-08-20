package fun.fengwk.convention4j.springboot.starter.cache.adapter;

import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public interface CacheAdapter {

    /**
     *
     * @param key
     * @param value 允许设置null
     * @param expireSeconds
     */
    void set(String key, String value, int expireSeconds);

    /**
     *
     * @param key
     * @return null表示空对象
     */
    String get(String key);

    /**
     * 批量设置指定的(key, value)列表
     *
     * @param kvMap 指定的key value映射
     * @param expireSeconds 所有key的过期时间/秒
     */
    void batchSet(Map<String, String> kvMap, int expireSeconds);

    /**
     * 批量获取指定的keys列表
     *
     * @param keys 指定的key列表
     * @return 存在的key对应的value映射，如果没有任何key存在则返回空映射
     */
    Map<String, String> batchGet(List<String> keys);

    /**
     * 批量删除指定的keys列表
     *
     * @param keys 指定的key列表
     */
    void batchDelete(List<String> keys);

}
