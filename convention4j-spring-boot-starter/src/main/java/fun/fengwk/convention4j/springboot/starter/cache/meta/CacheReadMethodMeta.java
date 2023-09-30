package fun.fengwk.convention4j.springboot.starter.cache.meta;

import fun.fengwk.convention4j.springboot.starter.cache.exception.CacheParseException;
import fun.fengwk.convention4j.springboot.starter.cache.util.CacheUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class CacheReadMethodMeta extends CacheMethodMeta {

    private static final Map<Class<?>, Supplier<ReturnDataContainer>> COLLECTION_RETURN_DATA_CONTAINER_FACTORY;

    static {
        Map<Class<?>, Supplier<ReturnDataContainer>> collectionReturnDataContainerFactory = new HashMap<>();
        collectionReturnDataContainerFactory.put(ArrayList.class, ArrayListContainer::new);
        collectionReturnDataContainerFactory.put(LinkedList.class, LinkedListContainer::new);
        collectionReturnDataContainerFactory.put(LinkedHashSet.class, LinkedHashSetContainer::new);
        COLLECTION_RETURN_DATA_CONTAINER_FACTORY = collectionReturnDataContainerFactory;
    }


    private final ResolvableType retType;
    private volatile Map<String, KeyMeta> toDataKeyMetaMap;
    private final boolean useIdQuery;
    protected final boolean returnDataType;
    protected final boolean returnMulti;
    protected final Supplier<ReturnDataContainer> returnDataContainerFactory;

    public CacheReadMethodMeta(
        Method method, CacheConfigMeta cacheConfig, List<MethodKeyMeta> cacheKeyMetas,
        ResolvableType retType, Class<?> dataClass, boolean useIdQuery) {
        super(method, cacheConfig, cacheKeyMetas);
        if (!cacheKeyMetas.isEmpty()) {
            boolean multi = cacheKeyMetas.get(0).isMulti();
            for (int i = 1; i < cacheKeyMetas.size(); i++) {
                if (cacheKeyMetas.get(i).isMulti() != multi) {
                    throw new CacheParseException("Multi property and non-multi property cannot be mixed");
                }
            }
        }

        // 检查返回值类型和原类型的关系
        // 如果返回值类型是Collection<DATA>或者DATA[]，最终可以转化为 idCacheKey -> data 的存储方式以便节省缓存存储空间
        // 对于返回值是Collection类型的，还需要检查当前是否支持该Collection容器，如果不支持也无法启用 idCacheKey -> data 的存储方式
        boolean returnDataType;
        boolean returnMulti = false;
        Supplier<ReturnDataContainer> returnDataContainerFactory = null;
        Class<?> retClass;
        if (retType.isArray() && (retClass = retType.getComponentType().resolve()) != null && retClass.isAssignableFrom(dataClass)) {
            returnDataType = true;
            returnMulti = true;
            returnDataContainerFactory = ArrayContainer::new;
        } else if (retType.getRawClass() != null && Collection.class.isAssignableFrom(retType.getRawClass())
            && (retClass = retType.getGeneric(0).resolve()) != null && retClass == dataClass) {
            returnDataType = true;
            returnMulti = true;
            for (Map.Entry<Class<?>, Supplier<ReturnDataContainer>> entry : COLLECTION_RETURN_DATA_CONTAINER_FACTORY.entrySet()) {
                if (retType.getRawClass() != null && retType.getRawClass().isAssignableFrom(entry.getKey())) {
                    returnDataContainerFactory = entry.getValue();
                    break;
                }
            }
        } else {
            returnDataType = Objects.equals(retType.resolve(), dataClass);
        }

        if (useIdQuery) {
            List<MethodKeyMeta> idKeyMetas = cacheKeyMetas.stream().filter(KeyMeta::isId).collect(Collectors.toList());
            if (idKeyMetas.isEmpty()) {
                throw new CacheParseException("Id query must have @IdKey occur in '" + method + "'");
            }
            if (!returnDataType) {
                throw new CacheParseException(
                    "Id query should return '" + dataClass + "' Array or Collection occur in '" + method + "'");
            }
            if (returnMulti && returnDataContainerFactory == null) {
                throw new CacheParseException(
                    "Id query support return '" + retType.getRawClass() + "' occur in '" + method + "'");
            }
        }

        this.retType = retType;
        this.useIdQuery = useIdQuery;
        this.returnDataType = returnDataType;
        this.returnMulti = returnMulti;
        this.returnDataContainerFactory = returnDataContainerFactory;
    }

    public void setToDataKeyMetaMap(Map<String, KeyMeta> toDataKeyMetaMap) {
        this.toDataKeyMetaMap = toDataKeyMetaMap;
    }

    public List<Map<String, Object>> buildKeyMapByData(Object data) {
        List<Object[]> kvsList = new ArrayList<>();
        for (MethodKeyMeta keyMeta : cacheKeyMetas) {
            // 获取与当前keyMeta对应的data中的keyMeta
            KeyMeta dataKeyMeta = toDataKeyMetaMap.get(keyMeta.getName());
            // 从data中获取value
            Object value = dataKeyMeta.getValue(data);
            // 构建keyName到dataValue的映射
            Object[] kvs = new Object[] { keyMeta.getName(), value, keyMeta.isSelective() };
            kvsList.add(kvs);
        }
        List<Map<String, Object>> keyMapList = new ArrayList<>();
        flatKeyMapList(kvsList, 0, new HashMap<>(), keyMapList);
        return keyMapList;
    }

    public List<Map<String, Object>> buildKeyMapByAnotherKeyMap(Map<String, Object> anotherKeyMap) {
        List<Object[]> kvsList = new ArrayList<>();
        for (KeyMeta keyMeta : cacheKeyMetas) {
            Object value = anotherKeyMap.get(keyMeta.getName());
            Object[] kvs = new Object[] { keyMeta.getName(), value, keyMeta.isSelective() };
            kvsList.add(kvs);
        }
        List<Map<String, Object>> keyMapList = new ArrayList<>();
        flatKeyMapList(kvsList, 0, new HashMap<>(), keyMapList);
        return keyMapList;
    }

    // Object[] { keyName, value, selective }
    private void flatKeyMapList(List<Object[]> kvsList, int idx,
                                Map<String, Object> curLinkMap, List<Map<String, Object>> collector) {
        if (idx == kvsList.size()) {
            collector.add(curLinkMap);
            return;
        }
        Object[] kvs = kvsList.get(idx);
        // kvs[2]为selective选项
        if ((boolean) kvs[2]) {
            // 如果当前kvs是selective需要同时考虑到当前value为null和非null的情况
            curLinkMap.put((String) kvs[0], kvs[1]);
            Map<String, Object> newLinkMap = new HashMap<>(curLinkMap);
            newLinkMap.put((String) kvs[0], null);
            flatKeyMapList(kvsList, idx + 1, curLinkMap, collector);
            flatKeyMapList(kvsList, idx + 1, newLinkMap, collector);
        } else {
            curLinkMap.put((String) kvs[0], kvs[1]);
            flatKeyMapList(kvsList, idx + 1, curLinkMap, collector);
        }
    }

    public String buildLv1CacheKey(Map<String, Object> keyMap) {
        return CacheUtils.buildLv1CacheKey(cacheConfig.getVersion(), method, keyMap);
    }

    public boolean canUseIdQuery() {
        return useIdQuery;
    }

    public boolean isReturnDataType() {
        return returnDataType;
    }

    public boolean isReturnMulti() {
        return returnMulti;
    }

    public ReturnDataContainer tryNewReturnContainer() {
        return returnDataContainerFactory == null ? null : returnDataContainerFactory.get();
    }

    public interface ReturnDataContainer {

        void collect(Object data);

        Object get(Class<?> dataClass);

    }

    public static class ArrayListContainer implements ReturnDataContainer {

        protected final ArrayList<Object> list = new ArrayList<>();

        @Override
        public void collect(Object data) {
            list.add(data);
        }

        @Override
        public Object get(Class<?> dataClass) {
            return list;
        }

    }

    public static class LinkedListContainer implements ReturnDataContainer {

        protected final LinkedList<Object> list = new LinkedList<>();

        @Override
        public void collect(Object data) {
            list.add(data);
        }

        @Override
        public Object get(Class<?> dataClass) {
            return list;
        }

    }

    public static class LinkedHashSetContainer implements ReturnDataContainer {

        protected final LinkedHashSet<Object> set = new LinkedHashSet<>();

        @Override
        public void collect(Object data) {
            set.add(data);
        }

        @Override
        public Object get(Class<?> dataClass) {
            return set;
        }

    }

    public static class ArrayContainer extends ArrayListContainer {

        @Override
        public Object get(Class<?> dataClass) {
            Object dataArr = Array.newInstance(dataClass, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(dataArr, i, list.get(i));
            }
            return dataArr;
        }

    }


}
