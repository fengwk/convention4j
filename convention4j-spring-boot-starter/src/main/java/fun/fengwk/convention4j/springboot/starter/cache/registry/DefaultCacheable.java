package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.common.cache.Cacheable;
import fun.fengwk.convention4j.common.cache.exception.CacheExecuteException;
import fun.fengwk.convention4j.common.util.Pair;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.meta.ListenKeyMeta;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Slf4j
public class DefaultCacheable<O> implements Cacheable<O> {

    private final Method method;
    private final String cacheVersion;
    private final List<DefaultListenKey> defaultListenKeyList;

    public DefaultCacheable(Method method, String cacheVersion, List<DefaultListenKey> defaultListenKeyList) {
        this.method = Objects.requireNonNull(method, "method cannot be null");
        this.cacheVersion = Objects.requireNonNull(cacheVersion, "cacheVersion cannot be null");
        this.defaultListenKeyList = Objects.requireNonNull(defaultListenKeyList, "defaultListenKeyList cannot be null");
    }

    @Override
    public String getCacheName() {
        return method.toString();
    }

    @Override
    public String getCacheVersion() {
        return cacheVersion;
    }

    @Override
    public List<TreeMap<String, Object>> extractListenKeyGroupListFromParams(Object[] params) {
        // 收集所有被@ListenKey注释的参数或内部字段
        int listValueSizeCheck = -1;
        Map<String /* listenKey */, Object /* simple type or simple type list */> valueMap = new HashMap<>();
        for (DefaultListenKey defaultListenKey : defaultListenKeyList) {
            Function<Object[], Object> extractFunc = defaultListenKey.getExtractListenKeyGroupListFromParams();
            Object value = extractFunc.apply(params);
            if (value == null && defaultListenKey.getListenKeyMeta().isRequired()) {
                log.error("ListenKey '{}' is required, but value is null.", defaultListenKey.getListenKeyMeta().getValue());
                throw new CacheExecuteException("ListenKey '%s' is required, but value is null.",
                    defaultListenKey.getListenKeyMeta().getValue());
            }
            // 将多值元素适配为List
            if (defaultListenKey.isMulti()) {
                value = defaultListenKey.adaptList(value);
            }
            if (value instanceof List) {
                List<?> listValue = (List<?>) value;
                if (listValueSizeCheck >= 0 && (listValue.size() != listValueSizeCheck)) {
                    log.error("Multi-values element annotated with @ListenKey must be same size, but value '{}' " +
                        "is not same size as {}.", value, listValueSizeCheck);
                    throw new CacheExecuteException("Multi-values element annotated with @ListenKey must be " +
                        "same size, but value '" + value + "' is not same size as " + listValueSizeCheck + ".");
                }
                listValueSizeCheck = listValue.size();
            }
            valueMap.put(defaultListenKey.getListenKeyMeta().getValue(), value);
        }

        // 构建所有要平铺的组
        List<TreeMap<String, Object>> listenKeyGroupList = new ArrayList<>();
        int repeat = listValueSizeCheck <= 0 ? 1 : listValueSizeCheck;
        for (int i = 0; i < repeat; i++) {
            listenKeyGroupList.add(new TreeMap<>());
        }

        // 构建所有平铺组
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String listenKey = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (list.isEmpty()) {
                    // 如果是空列表，则使用null代替value
                    listenKeyGroupList.get(0).put(listenKey, null);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        listenKeyGroupList.get(i).put(listenKey, list.get(i));
                    }
                }
            } else {
                for (Map<String, Object> nameValueGroup : listenKeyGroupList) {
                    nameValueGroup.put(listenKey, value);
                }
            }
        }

        return listenKeyGroupList;
    }

    @Override
    public List<TreeMap<String, Object>> extractListenKeyGroupListFromObj(O obj) {
        // 从对象中获取所有监听值，如果监听值中包含多值元素，则所有的监听值必须要
        List<Pair<ListenKeyMeta /* required */, Object /* simple type */>> metaValueList = new ArrayList<>();
        for (DefaultListenKey defaultListenKey : defaultListenKeyList) {
            PropertyPath extractFunc = defaultListenKey.getExtractListenKeyGroupListFromObj();
            Object value = extractFunc.getPropertyValue(obj);
            ListenKeyMeta listenKeyMeta = defaultListenKey.getListenKeyMeta();
            metaValueList.add(Pair.of(listenKeyMeta, value));
        }

        // 如果元素是非必须的，则需要同时考虑非空值和空值，需要编排和展开
        List<TreeMap<String, Object>> collector = new ArrayList<>();
        TreeMap<String, Object> listenKeyGroup = new TreeMap<>();
        collector.add(listenKeyGroup);
        expand(metaValueList, 0, listenKeyGroup, collector);
        return collector;
    }

    private void expand(List<Pair<ListenKeyMeta, Object>> metaValueList, int index,
                        TreeMap<String, Object> curListenKeyGroup, List<TreeMap<String, Object>> collector) {
        if (index >= metaValueList.size()) {
            return;
        }

        Pair<ListenKeyMeta, Object> metaValue = metaValueList.get(index);
        ListenKeyMeta listenKeyMeta = metaValue.getKey();
        Object value = metaValue.getValue();

        curListenKeyGroup.put(listenKeyMeta.getValue(), value);
        expand(metaValueList, index + 1, curListenKeyGroup, collector);

        if (!listenKeyMeta.isRequired()) {
            curListenKeyGroup.put(listenKeyMeta.getValue(), null);
            TreeMap<String, Object> nullValueListenKeyGroup = new TreeMap<>(curListenKeyGroup);
            collector.add(nullValueListenKeyGroup);
            expand(metaValueList, index + 1, nullValueListenKeyGroup, collector);
        }
    }

}
