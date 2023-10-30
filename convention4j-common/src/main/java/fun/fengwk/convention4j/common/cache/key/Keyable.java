package fun.fengwk.convention4j.common.cache.key;

/**
 * @author fengwk
 */
public interface Keyable {

    /**
     * 自定义将对象转换为缓存键。
     *
     * @return 缓存键。
     */
    String toKey();

}
