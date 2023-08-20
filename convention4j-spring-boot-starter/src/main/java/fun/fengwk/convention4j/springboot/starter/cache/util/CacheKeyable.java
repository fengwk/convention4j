package fun.fengwk.convention4j.springboot.starter.cache.util;

/**
 * @author fengwk
 */
public interface CacheKeyable {

    /**
     * 将对象转换为缓存键
     *
     * @return 缓存键
     */
    String toKey();

}
