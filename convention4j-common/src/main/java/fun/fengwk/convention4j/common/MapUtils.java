package fun.fengwk.convention4j.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class MapUtils {

    private MapUtils() {}

    /**
     * 使用指定键值对构建映射。
     *
     * @param k1
     * @param v1
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> newMap(K k1, V v1) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }

    /**
     * 使用指定键值对构建映射，该方法无法在编译期完成类型检查，因此可以优先使用其它的newMap方法。
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> newMap(Object... kvs) {
        Map<K, V> map = new HashMap<>();
        if (kvs != null && kvs.length > 0) {
            if (kvs.length % 2 != 0) {
                throw new IllegalArgumentException("unequal number of key and value");
            }
            for (int i = 0; i < kvs.length; i += 2) {
                map.put((K) kvs[i], (V) kvs[i+1]);
            }
        }
        return map;
    }

}
