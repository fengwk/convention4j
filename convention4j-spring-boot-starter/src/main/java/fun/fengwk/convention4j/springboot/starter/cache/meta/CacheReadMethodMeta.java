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

    public Map<String, Object> buildKeyMapByData(Object data) {
        return KeyMeta.buildKeyMap(cacheKeyMetas, k -> data, (k, r) -> {
            KeyMeta dataKeyMeta = toDataKeyMetaMap.get(k.getName());
            if (dataKeyMeta == null) {
                System.out.println("");
            }
            return dataKeyMeta.getValue(r);
        });
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
