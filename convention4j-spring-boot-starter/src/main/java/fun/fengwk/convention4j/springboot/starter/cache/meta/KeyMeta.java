package fun.fengwk.convention4j.springboot.starter.cache.meta;

import fun.fengwk.convention4j.springboot.starter.cache.exception.CacheParseException;
import fun.fengwk.convention4j.springboot.starter.cache.util.CacheUtils;
import lombok.Data;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Data
public class KeyMeta {

    private final boolean id;
    private final String name;
    private final boolean selective;
    private final Function<Object, Object> valueGetter;

    public Object getValue(Object root) {
        return valueGetter.apply(root);
    }

    public static <K extends KeyMeta> Map<String, Object> buildKeyMap(
        List<K> keyMetas, Function<K, Object> rootGetter, BiFunction<K, Object, Object> valueGetter) {

        Map<String, Object> keyMap = new HashMap<>();
        int sizeCheck = -1;
        for (K keyMeta : keyMetas) {
            // 找到keyMeta对应的根对象
            Object root = rootGetter.apply(keyMeta);
            // 将根对象转换为List
            List<Object> list = CacheUtils.adaptList(root);
            // 从跟对象中获取keyMeta对应的所有值
            List<Object> keyValues = new ArrayList<>();
            for (Object item : list) {
                keyValues.add(valueGetter.apply(keyMeta, item));
            }

            // 从每个根对象中获取的元素个数必须相等，此处进行检查
            if (sizeCheck != -1 && sizeCheck != keyValues.size()) {
                throw new CacheParseException("Multi property size must be equal");
            }
            sizeCheck = keyValues.size();

            // 如果元素只有一个则从List中解包
            Object value = keyValues.size() == 1 ? keyValues.get(0) : keyValues;
            keyMap.put(keyMeta.getName(), value);
        }
        return keyMap;
    }

    public static <K extends KeyMeta> List<Map<String, Object>> buildKeyMapList(
        List<K> keyMetas, Function<K, Object> rootGetter, BiFunction<K, Object, Object> valueGetter) {
        // 获取keyMap
        Map<String, Object> keyMap = buildKeyMap(keyMetas, rootGetter, valueGetter);
        if (keyMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 如果不是multi的情况则直接包装返回
        Object anyValue = keyMap.entrySet().iterator().next().getValue();
        if (!(anyValue instanceof Collection)) {
            return Collections.singletonList(keyMap);
        }

        // 将keyMap适配为keyMapList
        int size = ((Collection<?>) anyValue).size();
        Set<String> keyNames = keyMap.keySet();
        List<Map<String, Object>> keyMapList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<String, Object> keyMapItem = new HashMap<>();
            for (String keyName : keyNames) {
                List<?> list = (List<?>) keyMap.get(keyName);
                Object value = list.get(i);
                keyMapItem.put(keyName, value);
            }
            keyMapList.add(keyMapItem);
        }
        return keyMapList;
    }

    public static <K extends KeyMeta> List<K> merge(
        List<K> idKeyMetas, List<K> keyMetas) {
        idKeyMetas = new ArrayList<>(idKeyMetas);
        Set<String> existsDataKeyNames = idKeyMetas.stream()
                .map(KeyMeta::getName).collect(Collectors.toSet());
        for (K keyMeta : keyMetas) {
            if (existsDataKeyNames.add(keyMeta.getName())) {
                idKeyMetas.add(keyMeta);
            } else {
                throw new CacheParseException(
                        "Data type can not have two @Key or @IdKey annotation with same name: " + keyMeta.getName());
            }
        }
        return idKeyMetas;
    }

}
