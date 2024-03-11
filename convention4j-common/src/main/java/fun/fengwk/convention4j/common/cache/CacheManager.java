package fun.fengwk.convention4j.common.cache;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.common.cache.key.CacheKey;
import fun.fengwk.convention4j.common.cache.key.CacheKeyPrefix;
import fun.fengwk.convention4j.common.cache.key.KeyUtils;
import fun.fengwk.convention4j.common.cache.key.VersionKey;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class CacheManager<O> {

    protected final CacheFacade cacheFacade;
    protected volatile String cacheManagerVersion;
    protected final int cacheExpireSeconds;
    protected final CacheManagerMetrics cacheManagerMetrics;
    protected final ConcurrentMap<String /* cacheName */, Cacheable<O>> registry = new ConcurrentHashMap<>();

    public CacheManager(String cacheManagerVersion, CacheFacade cacheFacade, int cacheExpireSeconds,
                        CacheManagerMetrics cacheManagerMetrics) {
        if (cacheExpireSeconds <= 0) {
            throw new IllegalArgumentException("cacheExpireSeconds must be positive");
        }
        this.cacheManagerVersion = Objects.requireNonNull(cacheManagerVersion, "cacheManagerVersion cannot be null");
        this.cacheFacade = Objects.requireNonNull(cacheFacade, "cacheFacade cannot be null");
        this.cacheExpireSeconds = cacheExpireSeconds;
        this.cacheManagerMetrics = Objects.requireNonNull(cacheManagerMetrics, "cacheManagerMetrics cannot be null");
    }

    /**
     * 注册一个读缓存。
     *
     * @param cacheable 读缓存。
     */
    public void registerCacheable(Cacheable<O> cacheable) {
        registry.put(cacheable.getCacheName(), cacheable);
    }

    /**
     * 修改缓存管理器版本，该操作将清理当前管理器中的所有缓存信息。
     *
     * @param cacheManagerVersion 新的缓存管理器版本。
     */
    public void changeCacheManagerVersion(String cacheManagerVersion) {
        this.cacheManagerVersion = cacheManagerVersion;
    }

    /**
     * 通过缓存执行读操作。
     *
     * @param readFunc 查询函数。
     * @param params 查询函数的参数表。
     * @param typeOfT T类型。
     * @return 查询结果。
     * @param <T> 读取结果类型。
     */
    public <T> T read(String cacheName, Function<Object[], T> readFunc, Object[] params, Type typeOfT) {
        // 构建所有指向版本号的键
        Cacheable<O> cacheable = getCacheableRequired(cacheName);
        CacheKeyPrefix cacheKeyPrefix = buildCacheKeyPrefix(cacheable);
        List<TreeMap<String, Object>> listenKeyGroupList = cacheable.extractListenKeyGroupListFromParams(params);
        List<VersionKey> versionKeyList = buildVersionKeyList(cacheKeyPrefix, listenKeyGroupList);

        // 获取所有的缓存版本信息
        Set<String> versionZipKeySet = KeyUtils.toZipKeySet(versionKeyList);
        Map<String, String> cacheVersionMap = cacheFacade.batchGet(versionZipKeySet);

        if (cacheVersionMap.size() < versionZipKeySet.size()) {
            // 有部分cacheVersion已被失效，需要重新生成cacheVersion
            return reloadReadCache(cacheKeyPrefix, readFunc, params, versionZipKeySet);
        } else {
            // 尝试从缓存中获取被缓存的对象序列化形式
            CacheKey cacheKey = buildCacheKey(cacheKeyPrefix, cacheVersionMap.values(), params);
            String cacheZipKey = KeyUtils.toZipKey(cacheKey);
            String serializedObj = cacheFacade.get(cacheZipKey);
            if (serializedObj == null) {
                // 缓存失效，需要重新走查询流程
                return reloadReadCache(cacheKeyPrefix, readFunc, params, versionZipKeySet);
            } else {
                // 反序列化对象
                T obj = JsonUtils.fromJson(serializedObj, typeOfT);
                cacheManagerMetrics.read(cacheName, true);
                return obj;
            }
        }
    }

    /**
     * 执行写数据操作。
     *
     * @param objQueryFunc 通过参数查询所有对象。
     * @param writeFunc 写操作函数。
     * @param params 写操作参数。
     * @return 写操作返回值。
     * @param <T> 写操作返回值类型。
     */
    public <T> T write(Function<Object[], List<O>> objQueryFunc, Function<Object[], T> writeFunc, Object[] params) {
        List<O> objList = new ArrayList<>();
        T ret = transactionalWrite(objQueryFunc, writeFunc, params, objList);

        // 收集用于失效的缓存键
        Collection<Cacheable<O>> cacheables = registry.values();
        Set<String> invalidCacheKeySet = collectInvalidCacheKeySet(cacheables, objList);
        // 失效缓存
        cacheFacade.batchDelete(invalidCacheKeySet);

        return ret;
    }

    /**
     * 子类实现应保障其事务性
     */
    protected <T> T transactionalWrite(Function<Object[], List<O>> objQueryFunc, Function<Object[], T> writeFunc,
                                       Object[] params, List<O> objList) {
        // 先进行对象查询
        objList.addAll(NullSafe.of(objQueryFunc.apply(params)));
        // 执行写操作
        return writeFunc.apply(params);
    }

    /**
     * 从objList中收集要进行失效的缓存键。
     *
     * @param cacheables 可缓存的内容列表。
     * @param objList obj对象内容列表。
     * @return 要进行失效的缓存键。
     */
    protected Set<String> collectInvalidCacheKeySet(Collection<Cacheable<O>> cacheables, List<O> objList) {
        Set<String> invalidCacheKeySet = new HashSet<>();
        for (Cacheable<O> cacheable : cacheables) {
            for (O obj : objList) {
                CacheKeyPrefix cacheKeyPrefix = buildCacheKeyPrefix(cacheable);
                List<TreeMap<String, Object>> listenKeyGroupList = cacheable.extractListenKeyGroupListFromObj(obj);
                for (TreeMap<String, Object> listenKeyGroup : listenKeyGroupList) {
                    VersionKey versionKey = buildVersionKey(cacheKeyPrefix, listenKeyGroup);
                    String versionZipKey = KeyUtils.toZipKey(versionKey);
                    invalidCacheKeySet.add(versionZipKey);
                }
            }
        }
        return invalidCacheKeySet;
    }

    /**
     * 生成一个缓存版本号。
     *
     * @return 缓存版本号。
     */
    protected String generateVersion() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private <T> T reloadReadCache(CacheKeyPrefix cacheKeyPrefix, Function<Object[], T> readFunc,
                                  Object[] params, Set<String> versionZipKeySet) {
        // 该步骤需要在readFunc之前执行，才能保证缓存一致性
        Map<String, String> cacheVersionMap = regenerateCacheVersion(versionZipKeySet);
        // 由于缓存已被失效，所以需要重新进行数据查询
        T obj = readFunc.apply(params);
        // 缓存新的cacheKey到idList映射
        CacheKey cacheKey = buildCacheKey(cacheKeyPrefix, cacheVersionMap.values(), params);
        String cacheZipKey = KeyUtils.toZipKey(cacheKey);
        // GsonUtils会自动处理null对象
        String serializedObj = JsonUtils.toJson(obj);
        cacheFacade.set(cacheZipKey, serializedObj, cacheExpireSeconds);
        cacheManagerMetrics.read(cacheKeyPrefix.getCacheName(), false);
        return obj;
    }

    private Map<String, String> regenerateCacheVersion(Set<String> versionZipKeySet) {
        if (versionZipKeySet == null || versionZipKeySet.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> cacheVersionMap = new HashMap<>();
        for (String versionZipKey : versionZipKeySet) {
            cacheVersionMap.put(versionZipKey, generateVersion());
        }
        cacheFacade.batchSet(cacheVersionMap, cacheExpireSeconds);
        return cacheVersionMap;
    }

    private Cacheable<O> getCacheableRequired(String cacheName) {
        Cacheable<O> readCache = registry.get(cacheName);
        if (readCache == null) {
            throw new IllegalArgumentException(String.format("No ReadCache found for cacheName '%s'", cacheName));
        }
        return readCache;
    }

    private CacheKeyPrefix buildCacheKeyPrefix(Cacheable<O> cacheable) {
        return new CacheKeyPrefix(cacheManagerVersion, cacheable.getCacheName(), cacheable.getCacheVersion());
    }

    private VersionKey buildVersionKey(CacheKeyPrefix cacheKeyPrefix, TreeMap<String, Object> listenKeyGroup) {
        return new VersionKey(cacheKeyPrefix, listenKeyGroup);
    }

    private List<VersionKey> buildVersionKeyList(CacheKeyPrefix cacheKeyPrefix, List<TreeMap<String, Object>> listenKeyGroupList) {
        if (listenKeyGroupList == null || listenKeyGroupList.isEmpty()) {
            VersionKey versionKey = buildVersionKey(cacheKeyPrefix, null);
            return Collections.singletonList(versionKey);
        } else {
            List<VersionKey> versionKeyList = new ArrayList<>();
            for (TreeMap<String, Object> listenKeyGroup : listenKeyGroupList) {
                versionKeyList.add(buildVersionKey(cacheKeyPrefix, listenKeyGroup));
            }
            return versionKeyList;
        }
    }

    private CacheKey buildCacheKey(CacheKeyPrefix cacheKeyPrefix, Collection<String> versionKeys, Object[] params) {
        return new CacheKey(cacheKeyPrefix, versionKeys, params);
    }

}
