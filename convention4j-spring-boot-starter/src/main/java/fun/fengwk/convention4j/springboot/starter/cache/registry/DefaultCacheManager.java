package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.common.cache.CacheManager;
import fun.fengwk.convention4j.common.cache.Cacheable;
import fun.fengwk.convention4j.common.cache.exception.CacheExecuteException;
import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Slf4j
public class DefaultCacheManager<O> extends CacheManager<O> {

    private final WriteTransactionSupport writeTransactionSupport;
    private final ConcurrentMap<Method, String> readMethodCacheNameMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Method, DefaultWriteMethod<O>> defaultWriteMethodMap = new ConcurrentHashMap<>();

    public DefaultCacheManager(String cacheManagerVersion, CacheFacade cacheFacade, int cacheExpireSeconds,
                               CacheManagerMetrics cacheManagerMetrics, WriteTransactionSupport writeTransactionSupport) {
        super(cacheManagerVersion, cacheFacade, cacheExpireSeconds, cacheManagerMetrics);
        this.writeTransactionSupport = Objects.requireNonNull(
            writeTransactionSupport, "writeTransactionSupport cannot be null");
    }

    void registerReadMethod(Method readMethod, Cacheable<O> cacheable) {
        readMethodCacheNameMap.put(readMethod, cacheable.getCacheName());
        registerCacheable(cacheable);
    }

    void registerWriteMethod(DefaultWriteMethod<O> defaultWriteMethod) {
        defaultWriteMethodMap.put(defaultWriteMethod.getWriteMethod(), defaultWriteMethod);
    }

    public boolean isReadMethod(Method method) {
        return readMethodCacheNameMap.containsKey(method);
    }

    public boolean isWriteMethod(Method method) {
        return defaultWriteMethodMap.containsKey(method);
    }

    public <T> T read(Method readMethod, Function<Object[], T> readFunc, Object[] params, Type typeOfT) {
        String cacheName = readMethodCacheNameMap.get(readMethod);
        return read(cacheName, readFunc, params, typeOfT);
    }

    public <T> T write(Method writeMethod, Function<Object[], T> writeFunc, Object[] params) {
        DefaultWriteMethod<O> defaultWriteMethod = defaultWriteMethodMap.get(writeMethod);
        if (defaultWriteMethod == null) {
            log.error("Cannot find write method, writeMethod: {}", writeMethod);
            throw new CacheExecuteException("Cannot find write method");
        }
        return write(defaultWriteMethod.getObjQueryFunc(), writeFunc, params);
    }

    @Override
    protected <T> T transactionalWrite(Function<Object[], List<O>> objQueryFunc, Function<Object[], T> writeFunc,
                                       Object[] params, List<O> objList) {
        return writeTransactionSupport.transactionalWrite(
            () -> super.transactionalWrite(objQueryFunc, writeFunc, params, objList));
    }

}
