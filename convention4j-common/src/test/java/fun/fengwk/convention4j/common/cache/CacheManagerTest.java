package fun.fengwk.convention4j.common.cache;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.Ref;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.cache.metrics.LogCacheManagerMetrics;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @author fengwk
 */
public class CacheManagerTest {

    private static final String TEST_CACHE_NAME = "testCacheName";

    private final CacheManagerMetrics cacheManagerMetrics = new LogCacheManagerMetrics();
    private final CacheManager<UserBO> cacheManager = new CacheManager<>(
        "v1", new MemoryCacheFacade(), 10, cacheManagerMetrics);

    {
        cacheManager.registerCacheable(new Cacheable<UserBO>() {
            @Override
            public String getCacheName() {
                return TEST_CACHE_NAME;
            }
            @Override
            public String getCacheVersion() {
                return "v1";
            }
            @Override
            public List<TreeMap<String, Object>> extractListenKeyGroupListFromParams(Object[] params) {
                TreeMap<String, Object> m = new TreeMap<>();
                m.put("id", params[0]);
                m.put("name", params[1]);
                return Collections.singletonList(m);
            }
            @Override
            public List<TreeMap<String, Object>> extractListenKeyGroupListFromObj(UserBO obj) {
                Long id = obj.getId();
                String name = obj.getName();
                TreeMap<String, Object> m1 = new TreeMap<>();
                m1.put("id", id);
                m1.put("name", name);
                TreeMap<String, Object> m2 = new TreeMap<>();
                m2.put("id", null);
                m2.put("name", name);
                TreeMap<String, Object> m3 = new TreeMap<>();
                m3.put("id", id);
                m3.put("name", null);
                TreeMap<String, Object> m4 = new TreeMap<>();
                m4.put("id", null);
                m4.put("name", null);
                return Arrays.asList(m1, m2, m3, m4);
            }
        });
    }

    @Before
    public void reset() {
        cacheManagerMetrics.clear();
    }

    @Test
    public void test() {
        Ref<UserBO> store = Ref.of(null);

        UserBO userBO = query(store, 1L, "fengwk");
        assert userBO == null;
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 1;

        assert add(store, 1L, "fengwk") > 0;

        userBO = query(store, 1L, "fengwk");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 1L);
        assert Objects.equals(userBO.getName(), "fengwk");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 2;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 0;

        userBO = query(store, 1L, "fengwk");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 1L);
        assert Objects.equals(userBO.getName(), "fengwk");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 3;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 1;

        assert update(store, 1L, "fengwk", 2L, "fengwk2") > 0;

        userBO = query(store, 1L, "fengwk");
        assert userBO == null;
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 4;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 1;

        userBO = query(store, 2L, "fengwk2");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 2L);
        assert Objects.equals(userBO.getName(), "fengwk2");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 5;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 1;

        userBO = query(store, 2L, "fengwk2");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 2L);
        assert Objects.equals(userBO.getName(), "fengwk2");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 6;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 2;

        assert update(store, 2L, "fengwk2", 2L, null) > 0;

        userBO = query(store, 2L, null);
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 2L);
        assert Objects.equals(userBO.getName(), null);
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 7;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 2;

        userBO = query(store, 2L, null);
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 2L);
        assert Objects.equals(userBO.getName(), null);
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 8;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 3;

        assert update(store, 2L, null, 3L, "fengwk3") > 0;

        userBO = query(store, 3L, "fengwk3");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 3L);
        assert Objects.equals(userBO.getName(), "fengwk3");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 9;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 3;

        userBO = query(store, 3L, "fengwk3");
        assert userBO != null;
        assert Objects.equals(userBO.getId(), 3L);
        assert Objects.equals(userBO.getName(), "fengwk3");
        assert cacheManagerMetrics.getReadCount(TEST_CACHE_NAME) == 10;
        assert cacheManagerMetrics.getReadHitCount(TEST_CACHE_NAME) == 4;
    }

    private int add(Ref<UserBO> store, Object...args) {
        UserBO userBO = new UserBO();
        userBO.setId((Long) args[0]);
        userBO.setName((String) args[1]);
        return cacheManager.write(params -> NullSafe.wrap2List(userBO), params -> {
            store.setValue(userBO);
            return 1;
        }, args);
    }

    private int update(Ref<UserBO> store, Object...args) {
        return cacheManager.write(params -> NullSafe.wrap2List(testFind(store, params)), params -> {
            UserBO userBO = testFind(store, params);
            if (userBO == null) {
                return 0;
            } else {
                UserBO updatedUserBO = new UserBO();
                updatedUserBO.setId((Long) params[2]);
                updatedUserBO.setName((String) params[3]);
                store.setValue(updatedUserBO);
                return 1;
            }
        }, args);
    }

    private UserBO query(Ref<UserBO> store, Object...args) {
        return cacheManager.read(TEST_CACHE_NAME, params -> testFind(store, params),
            args, UserBO.class);
    }

    private UserBO testFind(Ref<UserBO> store, Object[] params) {
        UserBO userBO = store.getValue();
        if (userBO != null && Objects.equals(userBO.getId(), params[0]) && Objects.equals(userBO.getName(), params[1])) {
            return userBO;
        } else {
            return null;
        }
    }

}
