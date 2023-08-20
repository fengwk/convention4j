package fun.fengwk.convention4j.springboot.starter.cache.support;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>概念描述</p>
 * <ul>
 * <li>DATA：缓存数据。</li>
 * <li>ID其中存在能唯一标识缓存数据内容的属性，可通过INDEX唯一查找到其对应的缓存数据。</li>
 * <li>clearProperty：清理缓存的属性，是data的属性值，当这些属性发生改变时将影响已有的缓存内容。</li>
 * <li>clearKey：清理缓存的key，当clearKey包含的clearProperty发生变化时，清理所有与clearKey关联的缓存。</li>
 * <li>cacheKey：缓存key，指向缓存的id列表。</li>
 * <li>indexKey：每一个INDEX都对应一个indexKey，indexKey指向缓存的data。</li>
 * </ul>
 *
 * <p>缓存key组成</p>
 * <ul>
 * <li>clearKey：前缀+缓存名称（默认为方法全路径）+所有clearProperty</li>
 * <li>cacheKey：前缀+随机串+所有方法入参</li>
 * <li>cacheKey：前缀+idx</li>
 * </ul>
 *
 * <p>缓存结构</p>
 * {@code
 * ┌────────┐       ┌────────┐        ┌───────┐
 * │lv1Cache│──────►│cacheKey│───────►│id List│
 * └────────┘       └────────┘        └───────┘
 * ┌─────┐       ┌────┐
 * │idKey│──────►│data│
 * └─────┘       └────┘
 * }
 *
 * @author fengwk
 */
public interface CacheSupport<DATA, ID> {

    /* query */

    List<DATA> doListByIds(Collection<ID> ids);

    /* annotation and id converter */

    ID idKeyMapToId(Map<String, Object> idKeyMap);

    /* serialize and deserialize */

    String serializeId(ID id);

    ID deserializedId(String idCacheKey);

    String serializeData(DATA data);

    DATA deserializeData(String dataStr, Class<DATA> dataClass);

    String serializedReturnValue(Object returnValue);

    Object deserializedReturnValue(String returnValueStr, Type returnValueType);

}
