package fun.fengwk.convention4j.common.cache;

import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.common.cache.key.IndexCacheKey;
import fun.fengwk.convention4j.common.cache.key.IndexCacheKeyPrefix;
import fun.fengwk.convention4j.common.cache.key.IndexVersionKey;
import fun.fengwk.convention4j.common.cache.key.KeyUtils;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.util.ListUtils;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @param <O> 要缓存的对象类型。
 * @param <I> 索引类型，索引可以唯一定位到缓存的对象。
 * @author fengwk
 */
public abstract class AbstractIndexCacheManager<O, I> extends CacheManager<O> {

    protected final Class<O> objClass;
    protected final Class<I> indexClass;

    public AbstractIndexCacheManager(String cacheManagerVersion, CacheFacade cacheFacade, int cacheExpireSeconds,
                                     CacheManagerMetrics cacheManagerMetrics,
                                     Class<O> objClass, Class<I> indexClass) {
        super(cacheManagerVersion, cacheFacade, cacheExpireSeconds, cacheManagerMetrics);
        this.objClass = Objects.requireNonNull(objClass, "objClass cannot be null");
        this.indexClass = Objects.requireNonNull(indexClass, "indexClass cannot be null");
    }

    protected abstract boolean equalsIndex(I index1, I index2);

    protected abstract I getIndexFromObj(O obj);

    protected abstract List<O> listObjByIndexList(List<I> indexList);

    @Override
    protected Set<String> collectInvalidCacheKeySet(Collection<Cacheable<O>> cacheables, List<O> objList) {
        // 收集父类定义的所有用于失效缓存键
        Set<String> invalidCacheKeySet = super.collectInvalidCacheKeySet(cacheables, objList);
        // 获取所有索引的缓存键也作为失效键
        for (O obj : objList) {
            I index = getIndexFromObj(obj);
            IndexCacheKeyPrefix<I> indexCacheKeyPrefix = buildIndexCacheKeyPrefix(index);
            IndexVersionKey<I> indexVersionKey = buildIndexVersionKey(indexCacheKeyPrefix);
            String indexVersionZipKey = KeyUtils.toZipKey(indexVersionKey);
            invalidCacheKeySet.add(indexVersionZipKey);
        }
        return invalidCacheKeySet;
    }

    /**
     * 查询指定缓存的数据对象列表。
     *
     * @param cacheName 缓存名称。
     * @param readIndexListFunc 读取index列表的函数。
     * @param params 读取index列表的函数参数。
     * @return 返回指定缓存的数据列表。
     */
    public List<O> readObjList(String cacheName, Function<Object[], List<I>> readIndexListFunc, Object[] params) {
        ParameterizedType indexListType = $Gson$Types.newParameterizedTypeWithOwner(
            null, List.class, indexClass);
        List<I> indexList = read(cacheName, readIndexListFunc, params, indexListType);
        return listObj(indexList);
    }

    /**
     * 查询指定缓存的数据对象。
     *
     * @param cacheName 缓存名称。
     * @param readIndexFunc 读取index的函数。
     * @param params 读取index的函数参数。
     * @return 返回指定缓存的数据。
     */
    public O readObj(String cacheName, Function<Object[], I> readIndexFunc, Object[] params) {
        List<O> objList = readObjList(cacheName, ps -> NullSafe.wrap2List(readIndexFunc.apply(params)), params);
        return ListUtils.tryGetFirst(objList);
    }

    private List<O> listObj(List<I> indexList) {
        if (indexList == null || indexList.isEmpty()) {
            return Collections.emptyList();
        }

        // 去重index列表数据
        indexList = distinctIndexList(indexList);

        // 为indexList生成相应的indexCacheKeyPrefixList
        List<IndexCacheKeyPrefix<I>> indexCacheKeyPrefixList = new ArrayList<>();
        // 记录index到IndexCacheKeyPrefix的映射
        IdentityHashMap<I, IndexCacheKeyPrefix<I>> index2IndexCacheKeyPrefixMap = new IdentityHashMap<>();
        for (I index : indexList) {
            IndexCacheKeyPrefix<I> indexCacheKeyPrefix = buildIndexCacheKeyPrefix(index);
            indexCacheKeyPrefixList.add(indexCacheKeyPrefix);
            index2IndexCacheKeyPrefixMap.put(index, indexCacheKeyPrefix);
        }

        // 转为indexVersionZipKeySet
        Set<String> indexVersionZipKeySet = new HashSet<>();
        // 记录index到versionZipKey的映射
        IdentityHashMap<I, String> index2IndexVersionZipKeyMap = new IdentityHashMap<>();
        for (IndexCacheKeyPrefix<I> indexCacheKeyPrefix : indexCacheKeyPrefixList) {
            IndexVersionKey<I> indexVersionKey = buildIndexVersionKey(indexCacheKeyPrefix);
            String indexVersionZipKey = KeyUtils.toZipKey(indexVersionKey);
            indexVersionZipKeySet.add(indexVersionZipKey);
            index2IndexVersionZipKeyMap.put(indexCacheKeyPrefix.getIndex(), indexVersionZipKey);
        }

        // 获取所有存在的index缓存版本信息
        Map<String, String> indexCacheVersionMap = cacheFacade.batchGet(indexVersionZipKeySet);
        // 收集缺失的版本信息并重新设置缓存
        Map<String, String> missedIndexCacheVersionMap = new HashMap<>();
        // 存储存在版本号的indexCacheZipKeySet，后边会用这个信息查询缓存获取存在的data信息
        Set<String> existsIndexCacheZipKeySet = new HashSet<>();
        // 记录index到indexCacheZipKey的映射
        IdentityHashMap<I, String> index2IndexCacheZipKeyMap = new IdentityHashMap<>();
        for (I index : indexList) {
            String indexVersionZipKey = index2IndexVersionZipKeyMap.get(index);
            String indexCacheVersion = indexCacheVersionMap.get(indexVersionZipKey);
            IndexCacheKeyPrefix<I> indexCacheKeyPrefix = index2IndexCacheKeyPrefixMap.get(index);
            if (indexCacheVersion != null) {
                // 存在index缓存版本信息
                IndexCacheKey<I> indexCacheKey = buildIndexCacheKey(indexCacheKeyPrefix, indexCacheVersion);
                String indexCacheZipKey = KeyUtils.toZipKey(indexCacheKey);
                existsIndexCacheZipKeySet.add(indexCacheZipKey);
                index2IndexCacheZipKeyMap.put(index, indexCacheZipKey);
            } else {
                // index缓存版本信息缺失
                // 预生成版本信息记录到missedIndexCacheVersionMap中
                indexCacheVersion = generateVersion();
                missedIndexCacheVersionMap.put(indexVersionZipKey, indexCacheVersion);
                // 通过版本信息构建出indexCacheZipKey并写入到index2IndexCacheZipKeyMap中
                IndexCacheKey<I> indexCacheKey = buildIndexCacheKey(indexCacheKeyPrefix, indexCacheVersion);
                String indexCacheZipKey = KeyUtils.toZipKey(indexCacheKey);
                index2IndexCacheZipKeyMap.put(index, indexCacheZipKey);
            }
        }
        // 写入预生成的版本信息
        if (!missedIndexCacheVersionMap.isEmpty()) {
            cacheFacade.batchSet(missedIndexCacheVersionMap, cacheExpireSeconds);
        }
        // 获取已存在的obj数据
        IdentityHashMap<I, O> objMap = new IdentityHashMap<>();
        List<I> missedIndexList = new ArrayList<>();
        if (!existsIndexCacheZipKeySet.isEmpty()) {
            // indexCacheZipKey -> objJson
            Map<String, String> objJsonMap = cacheFacade.batchGet(existsIndexCacheZipKeySet);
            // 需要找出所有缺失的obj数据
            for (I index : indexList) {
                String indexCacheZipKey = index2IndexCacheZipKeyMap.get(index);
                String objJson = objJsonMap.get(indexCacheZipKey);
                if (objJson != null) {
                    O obj = JsonUtils.fromJson(objJson, objClass);
                    objMap.put(index, obj);
                } else {
                    missedIndexList.add(index);
                }
            }
        }

        // 对于缺失数据的index要重新进行数据查询
        List<O> foundDataList = listObjByIndexList(missedIndexList);
        // indexCacheZipKey -> dataJson
        IdentityHashMap<String, String> missedDataJsonMap = new IdentityHashMap<>();
        for (I missedIndex : missedIndexList) {
            O found = findObj(foundDataList, missedIndex);
            // 记录到总的dataMap中
            objMap.put(missedIndex, found);
            // 记录到missedDataJsonMap，后续写入缓存
            String indexCacheZipKey = index2IndexCacheZipKeyMap.get(missedIndex);
            // 此处将缓存空对象为"null"
            String dataJson = JsonUtils.toJson(found);
            missedDataJsonMap.put(indexCacheZipKey, dataJson);
        }
        // 写入缺失的data数据到缓存
        if (!missedDataJsonMap.isEmpty()) {
            cacheFacade.batchSet(missedDataJsonMap, cacheExpireSeconds);
        }

        // 组装要返回的数据
        List<O> dataList = new ArrayList<>();
        for (I index : indexList) {
            O data = objMap.get(index);
            if (data != null) {
                dataList.add(data);
            }
        }
        return dataList;
    }

    private List<I> distinctIndexList(List<I> indexList) {
        List<I> distinctIndexList = new ArrayList<>();
        for (I index : indexList) {
            if (!containsIndex(distinctIndexList, index)) {
                distinctIndexList.add(index);
            }
        }
        return distinctIndexList;
    }

    private boolean containsIndex(List<I> indexList, I index) {
        for (I listIndex : indexList) {
            if (equalsIndex(listIndex, index)) {
                return true;
            }
        }
        return false;
    }

    private O findObj(List<O> objList, I index) {
        for (O data : objList) {
            if (equalsIndex(index, getIndexFromObj(data))) {
                return data;
            }
        }
        return null;
    }

    private IndexCacheKeyPrefix<I> buildIndexCacheKeyPrefix(I index) {
        return new IndexCacheKeyPrefix<>(cacheManagerVersion, index);
    }

    private IndexVersionKey<I> buildIndexVersionKey(IndexCacheKeyPrefix<I> indexCacheKeyPrefix) {
        return new IndexVersionKey<>(indexCacheKeyPrefix);
    }

    private IndexCacheKey<I> buildIndexCacheKey(IndexCacheKeyPrefix<I> indexCacheKeyPrefix, String indexCacheVersion) {
        return new IndexCacheKey<>(indexCacheKeyPrefix, indexCacheVersion);
    }

}
