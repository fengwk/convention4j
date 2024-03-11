package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.common.reflect.TypeResolver;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
public class ConventionCheckerFinder {

    private final ConventionCheckerProvider provider;
    private volatile VersionCache versionCache;

    public ConventionCheckerFinder(ConventionCheckerProvider provider) {
        this.provider = provider;
        this.versionCache = buildVersionCache(provider.getVersionCheckerList());
    }

    public <T> ConventionChecker<T> getChecker(Class<T> valueClass) {
        VersionCache versionCache = this.versionCache;
        if (!Objects.equals(versionCache.version, provider.version())) {
            synchronized (this) {
                versionCache = this.versionCache;
                VersionCheckerList versionCheckerList = provider.getVersionCheckerList();
                if (!Objects.equals(versionCache.version, versionCheckerList.getVersion())) {
                    versionCache = buildVersionCache(versionCheckerList);
                    this.versionCache = versionCache;
                }
            }
        }
        return doGetChecker(valueClass, versionCache);
    }

    private VersionCache buildVersionCache(VersionCheckerList versionCheckerList) {
        Map<Type, ConventionChecker<?>> cache = new HashMap<>();
        for (ConventionChecker<?> checker : NullSafe.of(versionCheckerList.getCheckers())) {
            TypeResolver tr = new TypeResolver(checker.getClass()).as(ConventionChecker.class);
            if (!tr.isParameterizedType()) {
                throw new IllegalStateException("ConventionChecker must be parameterized type: "
                    + checker.getClass());
            }
            ParameterizedType pt = tr.asParameterizedType();
            Type type = pt.getActualTypeArguments()[0];
            cache.put(type, checker);
        }
        return new VersionCache(versionCheckerList.getVersion(), cache);
    }

    private static <T> ConventionChecker<T> doGetChecker(Class<T> valueClass, VersionCache versionCache) {
        return (ConventionChecker<T>) versionCache.cache.get(valueClass);
    }

    static class VersionCache {

        final String version;
        final Map<Type, ConventionChecker<?>> cache;

        VersionCache(String version, Map<Type, ConventionChecker<?>> cache) {
            this.version = version;
            this.cache = cache;
        }
    }

}
