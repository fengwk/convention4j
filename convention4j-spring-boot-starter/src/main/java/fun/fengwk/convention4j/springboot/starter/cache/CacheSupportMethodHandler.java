package fun.fengwk.convention4j.springboot.starter.cache;

import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.springboot.starter.cache.adapter.CacheAdapter;
import fun.fengwk.convention4j.springboot.starter.cache.meta.*;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.CacheSupportMetrics;
import fun.fengwk.convention4j.springboot.starter.cache.support.CacheSupport;
import fun.fengwk.convention4j.springboot.starter.cache.util.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.ResolvableType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class CacheSupportMethodHandler {

    private final ThreadLocal<Boolean> ignoreCache = ThreadLocal.withInitial(() -> false);
    private final CacheAdapter cacheAdapter;
    private final CacheSupportMetrics cacheSupportMetrics;

    public CacheSupportMethodHandler(CacheAdapter cacheAdapter, CacheSupportMetrics cacheSupportMetrics) {
        this.cacheAdapter = cacheAdapter;
        this.cacheSupportMetrics = cacheSupportMetrics;
    }

    public <DATA, ID> Object handleCacheWriteMethod(
        MethodInvocation invocation,
        CacheSupportMeta supportMeta,
        CacheWriteMethodMeta writeMethodMeta) throws Throwable {

        if (ignoreCache.get()) {
            return invocation.proceed();
        }

        // 获取仓储
        CacheSupport<DATA, ID> support = (CacheSupport<DATA, ID>) supportMeta.getSupport();

        // 获取原来的idList和dataList
        List<Map<String, Object>> idKeyMapList;
        List<Map<String, Object>> keyMapList;
        if (writeMethodMeta.isMulti()) {
            idKeyMapList = writeMethodMeta.buildIdKeyMapListByParameters(invocation.getArguments());
            keyMapList = writeMethodMeta.buildKeyMapListByParameters(invocation.getArguments());
        } else {
            Map<String, Object> idKeyMap = writeMethodMeta.buildIdKeyMapByParameters(invocation.getArguments());
            idKeyMapList = Collections.singletonList(idKeyMap);
            Map<String, Object> keyMap = writeMethodMeta.buildKeyMapByParameters(invocation.getArguments());
            keyMapList = Collections.singletonList(keyMap);
        }
        List<ID> idList = idKeyMapList.stream().map(support::idKeyMapToId).collect(Collectors.toList());
        List<DATA> dataList = listByIdsWithCache(supportMeta, writeMethodMeta, idList);

        // 执行原方法
        Object ret = invocation.proceed();

        // 收集要清理的idCacheKey列表
        List<String> idCacheKeyList = idList.stream()
            .map(support::serializeId)
            .map(idStr -> CacheUtils.buildIdCacheKey(writeMethodMeta.getCacheConfig().getVersion(), idStr))
            .collect(Collectors.toList());

        // 收集要清理的lv1CacheKey列表
        List<String> lv1CacheKeyList = new ArrayList<>();
        for (Object data : dataList) {
            for (CacheReadMethodMeta readMethodMeta : supportMeta.getCacheReadMethodMetas()) {
                for (Map<String, Object> keyMap : readMethodMeta.buildKeyMapByData(data)) {
                    String lv1CacheKey = readMethodMeta.buildLv1CacheKey(keyMap);
                    lv1CacheKeyList.add(lv1CacheKey);
                }
            }
        }

        // 如果无法通过id查询到原对象，可能是新增，因此需要失效所有的新增键
        for (Map<String, Object> keyMap : keyMapList) {
            for (CacheReadMethodMeta readMethodMeta : supportMeta.getCacheReadMethodMetas()) {
                for (Map<String, Object> readKeyMap : readMethodMeta.buildKeyMapByAnotherKeyMap(keyMap)) {
                    String lv1CacheKey = readMethodMeta.buildLv1CacheKey(readKeyMap);
                    lv1CacheKeyList.add(lv1CacheKey);
                }
            }
        }

        // 清理所有关联key
        Set<String> allDeleteKeys = new HashSet<>(idCacheKeyList);
        allDeleteKeys.addAll(lv1CacheKeyList);
        if (!allDeleteKeys.isEmpty()) {
            try {
                cacheAdapter.batchDelete(allDeleteKeys);
            } catch (Throwable ex) {
                // 此处异常将导致缓存不一致
                log.error("Clear cache error, maybe cache inconsistent, method: {}, l1CacheKeyList: {}",
                    invocation.getMethod(), lv1CacheKeyList, ex);
            }
        }

        return ret;
    }

    public <DATA, ID> Object handleCacheReadMethod(
        MethodInvocation invocation,
        CacheSupportMeta supportMeta,
        CacheReadMethodMeta readMethodMeta) throws Throwable {

        if (ignoreCache.get()) {
            return invocation.proceed();
        }

        // 获取仓储和配置信息
        CacheSupport<DATA, ID> support = (CacheSupport<DATA, ID>) supportMeta.getSupport();
        CacheConfigMeta configMeta = readMethodMeta.getCacheConfig();

        Class<?> dataClass = supportMeta.getDataClass();
        ResolvableType retType = readMethodMeta.getRetType();
        boolean isReturnDataType = readMethodMeta.isReturnDataType();
        boolean isReturnMulti = readMethodMeta.isReturnMulti();
        CacheReadMethodMeta.ReturnDataContainer retDataContainer = readMethodMeta.tryNewReturnContainer();

        List<Map<String, Object>> keyMapList;
        Object[] args = invocation.getArguments();
        if (readMethodMeta.isMulti()) {
            keyMapList = readMethodMeta.buildKeyMapListByParameters(args);
        } else {
            Map<String, Object> keyMap = readMethodMeta.buildKeyMapByParameters(args);
            keyMapList = Collections.singletonList(keyMap);
        }

        // 如果只有idKey，且没有更多参数了，且返回值类型是DATA类型，则直接通过idCacheKey获取数据
        if ((isReturnDataType && (!isReturnMulti || retDataContainer != null)) && readMethodMeta.canUseIdQuery()) {
            List<ID> idList = keyMapList.stream().map(support::idKeyMapToId).collect(Collectors.toList());
            List<Object> dataList = listByIdsWithCache(supportMeta, readMethodMeta, idList);
            return adaptReturnData(isReturnMulti, dataList, retDataContainer, dataClass);
        }

        List<String> lv1CacheKeyList = keyMapList.stream()
            .map(readMethodMeta::buildLv1CacheKey).sorted(String::compareTo).collect(Collectors.toList());

        Map<String, String> lv1CacheKey2UuidMap;
        try {
            lv1CacheKey2UuidMap = cacheAdapter.batchGet(lv1CacheKeyList);
        } catch (Throwable ex) {
            log.warn("Get lv1CacheKeyList error, downgrade, method: {}", invocation.getMethod(), ex);
            return invocation.proceed();
        }
        // lv1CacheKey未命中，重新设置
        if (lv1CacheKey2UuidMap.size() < lv1CacheKeyList.size()) {
            lv1CacheKey2UuidMap = new HashMap<>();
            for (String lv1CacheKey : lv1CacheKeyList) {
                lv1CacheKey2UuidMap.put(lv1CacheKey, UUID.randomUUID().toString().replace("-", ""));
            }
            try {
                cacheAdapter.batchSet(lv1CacheKey2UuidMap, configMeta.getExpireSeconds());
            } catch (Throwable ex) {
                log.warn("Set lv1CacheKey error, downgrade, method: {}", invocation.getMethod(), ex);
                return invocation.proceed();
            }
        }

        List<String> uuidList = lv1CacheKeyList.stream().map(lv1CacheKey2UuidMap::get).collect(Collectors.toList());
        String lv2CacheKey = CacheUtils.buildLv2CacheKey(configMeta.getVersion(), uuidList, args);
        String lv2CacheValue;
        try {
            lv2CacheValue = cacheAdapter.get(lv2CacheKey);
        } catch (Throwable ex) {
            log.warn("Get lv2CacheKey error, downgrade, method: {}", invocation.getMethod(), ex);
            return invocation.proceed();
        }

        if (lv2CacheValue == null) {
            // 无缓存情况，执行方法加载缓存
            Object retValue = invocation.proceed();

            if (isReturnDataType && (!isReturnMulti || retDataContainer != null)) {
                // 如果返回类型是DATA，存储 id -> data 的映射结构
                List<DATA> retValueAdapter = CacheUtils.adaptList(retValue);
                Map<String, String> kvMap = new HashMap<>();
                List<String> idStrList = new ArrayList<>();
                // 存储 idCacheKey -> dataStr 的映射
                for (DATA data : retValueAdapter) {
                    Map<String, Object> idKeyMap = supportMeta.buildIdKeyMapByData(data);
                    ID id = support.idKeyMapToId(idKeyMap);
                    String idStr = support.serializeId(id);
                    String idCacheKey = CacheUtils.buildIdCacheKey(configMeta.getVersion(), idStr);
                    kvMap.put(idCacheKey, support.serializeData(data));
                    idStrList.add(idStr);
                }
                // 存储 lv2CacheKey -> idCacheKeyListJson 的映射
                String idStrListJson = GsonUtils.toJson(idStrList);
                kvMap.put(lv2CacheKey, idStrListJson);
                // 批量存储
                try {
                    cacheAdapter.batchSet(kvMap, configMeta.getExpireSeconds());
                } catch (Throwable ex) {
                    log.warn("Batch set lv2 cache error, downgrade, method: {}", readMethodMeta.getMethod(), ex);
                }
            } else {
                // 如果返回类型不是DATA类型，直接存储返回值
                String retValueStr = support.serializedReturnValue(retValue);
                try {
                    cacheAdapter.set(lv2CacheKey, retValueStr, configMeta.getExpireSeconds());
                } catch (Throwable ex) {
                    log.warn("Batch set lv2 cache error, downgrade, method: {}", readMethodMeta.getMethod(), ex);
                }
            }

            try {
                cacheSupportMetrics.call(readMethodMeta.getMethod(), false, false);
            } catch (Throwable ex) {
                log.warn("Record metrics error", ex);
            }

            // 完成缓存后直接返回
            return retValue;
        }

        // 有缓存情况，根据返回值类型进行处理
        if (isReturnDataType && (!isReturnMulti || retDataContainer != null)) {
            List<String> idStrList = GsonUtils.fromJson(lv2CacheValue, new TypeToken<List<String>>() {}.getType());
            List<ID> idList = idStrList.stream().map(support::deserializedId).collect(Collectors.toList());
            List<DATA> dataList = listByIdsWithCache(supportMeta, readMethodMeta, idList);
            return adaptReturnData(isReturnMulti, dataList, retDataContainer, dataClass);
        } else {
            return support.deserializedReturnValue(lv2CacheValue, retType.getType());
        }
    }

    private static <DATA> Object adaptReturnData(boolean retMulti, List<DATA> dataList,
                                                 CacheReadMethodMeta.ReturnDataContainer retDataContainer, Class<?> dataClass) {
        if (retMulti) {
            for (DATA data : dataList) {
                retDataContainer.collect(data);
            }
            return retDataContainer.get(dataClass);
        } else {
            return dataList.isEmpty() ? null : dataList.get(0);
        }
    }

    private <DATA, ID> List<DATA> listByIdsWithCache(
            CacheSupportMeta supportMeta, CacheMethodMeta methodMeta, List<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        CacheSupport<DATA, ID> support = (CacheSupport<DATA, ID>) supportMeta.getSupport();
        Class<DATA> dataClass = (Class<DATA>) supportMeta.getDataClass();

        List<String> idStrList = new ArrayList<>();
        Set<String> idCacheKeySet = new HashSet<>();
        Map<String, ID> idCacheKey2IdMap = new HashMap<>();
        Map<String, String> idCacheKey2IdStrMap = new HashMap<>();
        for (ID id : ids) {
            String idStr = support.serializeId(id);
            String idCacheKey = CacheUtils.buildIdCacheKey(methodMeta.getCacheConfig().getVersion(), idStr);
            idStrList.add(idStr);
            idCacheKeySet.add(idCacheKey);
            idCacheKey2IdMap.put(idCacheKey, id);
            idCacheKey2IdStrMap.put(idCacheKey, idStr);
        }

        // 获取所有对应的dataStr
        Map<String, String> idCacheKey2DataStrMap = cacheAdapter.batchGet(
            new ArrayList<>(idCacheKeySet));

        // 构建 id -> data 的映射
        Map<String, DATA> idStr2DataMap = idCacheKey2DataStrMap.entrySet().stream()
            .collect(Collectors.toMap(e -> idCacheKey2IdStrMap.get(e.getKey()),
                e -> support.deserializeData(e.getValue(), dataClass), (o, n) -> o));

        // 检查那些id是没有被缓存的，如果存在没被缓存的id则需要去support中查询
        idCacheKeySet.removeAll(idCacheKey2DataStrMap.keySet());
        if (idCacheKeySet.isEmpty()) {
            try {
                cacheSupportMetrics.call(methodMeta.getMethod(), false, true);
            } catch (Throwable ex) {
                log.warn("Record metrics error", ex);
            }
        } else {
            List<ID> needQueryIdList = idCacheKeySet.stream()
                .map(idCacheKey2IdMap::get).collect(Collectors.toList());
            List<DATA> dataList;
            try {
                // 防止重入缓存拦截
                ignoreCache.set(true);
                dataList = support.doListByIds(needQueryIdList);
            } finally {
                ignoreCache.set(false);
            }
            // 通过新查询到的dataList构建缓存的键值进行存储
            Map<String, String> needCacheIdCacheKey2DataStrMap = new HashMap<>();
            for (DATA data : dataList) {
                Map<String, Object> idKeyMap = supportMeta.buildIdKeyMapByData(data);
                ID id = support.idKeyMapToId(idKeyMap);
                String idStr = support.serializeId(id);
                String idCacheKey = CacheUtils.buildIdCacheKey(methodMeta.getCacheConfig().getVersion(), idStr);
                needCacheIdCacheKey2DataStrMap.put(idCacheKey, support.serializeData(data));
                // 将新的数据也添加到id2DataMap中
                idStr2DataMap.put(idStr, data);
            }

            try {
                cacheAdapter.batchSet(needCacheIdCacheKey2DataStrMap, methodMeta.getCacheConfig().getExpireSeconds());
            } catch (Throwable ex) {
                // 缓存存储失败可以直接降级，数据正常返回即可，不影响当前接口语义
                log.warn("Batch set id->data cache error, downgrade", ex);
            }

            try {
                cacheSupportMetrics.call(methodMeta.getMethod(), idCacheKeySet.size() < ids.size(), false);
            } catch (Throwable ex) {
                log.warn("Record metrics error", ex);
            }
        }

        // 组装最终的结果
        List<DATA> result = new ArrayList<>();
        for (String idStr : idStrList) {
            DATA data = idStr2DataMap.get(idStr);
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }

}
