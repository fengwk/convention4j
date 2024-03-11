package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.common.cache.exception.CacheInitializationException;
import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.meta.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static fun.fengwk.convention4j.springboot.starter.cache.annotation.meta.CacheAnnotationMetaReader.*;
import static fun.fengwk.convention4j.springboot.starter.cache.registry.CacheInitializationUtils.*;

/**
 * @author fengwk
 */
@Slf4j
public class DefaultCacheManagerRegistry implements BeanPostProcessor {

    private final ConcurrentMap<Class<?>, DefaultCacheManager<?>> registry = new ConcurrentHashMap<>();
    private final BeanFactory beanFactory;
    private final CacheFacade cacheFacade;
    private final CacheManagerMetrics cacheManagerMetrics;

    public DefaultCacheManagerRegistry(BeanFactory beanFactory, CacheFacade cacheFacade, CacheManagerMetrics cacheManagerMetrics) {
        this.beanFactory = Objects.requireNonNull(beanFactory, "beanFactory cannot be null");
        this.cacheFacade = Objects.requireNonNull(cacheFacade, "cacheFacade cannot be null");
        this.cacheManagerMetrics = Objects.requireNonNull(cacheManagerMetrics, "cacheManagerMetrics cannot be null");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        CacheSupportClassFinder finder = new CacheSupportClassFinder();
        finder.find(bean);
        Class<?> cacheSupportClass = finder.getCacheSupportClass();
        if (cacheSupportClass != null) {
            CacheSupportMeta cacheSupportMeta = finder.getCacheSupportMeta();
            register(bean, cacheSupportClass, cacheSupportMeta);
        }
        return bean;
    }

    public DefaultCacheManager<?> getCacheManager(Object bean) {
        CacheSupportClassFinder finder = new CacheSupportClassFinder();
        finder.find(bean);
        Class<?> cacheSupportClass = finder.getCacheSupportClass();
        return registry.get(cacheSupportClass);
    }

    private <O> void register(Object bean, Class<?> cacheSupportClass, CacheSupportMeta cacheSupportMeta) {
        DefaultCacheManager<O> cacheManager = new DefaultCacheManager<>(
            cacheSupportMeta.getVersion(), cacheFacade, cacheSupportMeta.getExpireSeconds(), cacheManagerMetrics,
            beanFactory.getBean(cacheSupportMeta.getWriteTransactionSupport()));
        registerMethods(cacheManager, cacheSupportClass, cacheSupportMeta.getObjClass(), bean);
        registry.put(cacheSupportClass, cacheManager);
    }

    private <O> void registerMethods(DefaultCacheManager<O> cacheManager,
                                     Class<?> cacheSupportClass, Class<?> objClass, Object bean) {
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(cacheSupportClass);
        for (Method method : allDeclaredMethods) {
            if (!tryRegisterReadMethod(cacheManager, objClass, method)) {
                tryRegisterWriteMethod(cacheManager, objClass, method, cacheSupportClass, bean);
            }
        }
    }

    private <O> boolean tryRegisterReadMethod(
        DefaultCacheManager<O> cacheManager, Class<?> objClass, Method method) {
        ReadMethodMeta readMethodMeta = findReadMethodMeta(method);
        if (readMethodMeta == null) {
            return false;
        }

        String cacheName = StringUtils.isEmpty(readMethodMeta.getName()) ?
            method.getName() : readMethodMeta.getName();
        String cacheVersion = readMethodMeta.getVersion();

        // 查找所有带有@ListenKey的字段
        List<DefaultListenKey> collector = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            ResolvableType pType = ResolvableType.forMethodParameter(method, i);
            ListenKeyMeta listenKeyMeta = findListenKeyMeta(parameter);
            if (listenKeyMeta != null) {
                // @ListenKey仅支持注释简单类型或简单的可迭代类型
                if (!isSimplePropertyOrIterableSimpleProperty(pType)) {
                    log.error("Parameter annotated with @ListenKey only support simple types or collections of simple types, " +
                        "listenKeyMeta: {}, pType: {}", listenKeyMeta, pType);
                    throw new CacheInitializationException(
                        "Parameter annotated with @ListenKey only support simple types or collections of simple types.");
                }
                // 可以直接从参数表中获取到@ListenKey的情况
                final int index = i;
                DefaultListenKey defaultListenKey = new DefaultListenKey(
                    listenKeyMeta, params -> params[index],
                    parseListenKeyExpr(objClass, listenKeyMeta.getValue()),
                    pType);
                collector.add(defaultListenKey);
            } else {
                // 如果无法从参数表中获取到@ListenKey，则需要深入参数对象当中查找@ListenKey
                // 仅支持javaBean对象进行深入查询，因此对复杂对象进行排除
                if (isJavaBean(pType.resolve())) {
                    // 检索所有get方法和字段，检查@ListenKey注解，方法优先于字段
                    final int index = i;
                    Set<ResolvableType> visited = new HashSet<>();
                    visited.add(pType);
                    scanAndCollectListenKeyField(pType, collector,
                        params -> params[index], objClass, visited);
                }
            }
        }

        // 检查是否存在重复的@ListenKey value
        Set<String> listenKeyValues = new HashSet<>();
        for (DefaultListenKey defaultListenKey : collector) {
            String value = defaultListenKey.getListenKeyMeta().getValue();
            if (!listenKeyValues.add(value)) {
                log.error("Duplicate @ListenKey value, value: {}", value);
                throw new CacheInitializationException("Duplicate @ListenKey value '" + value + "'.");
            }
        }

        // 注册读方法
        cacheManager.registerReadMethod(
            method, new DefaultCacheable<>(method, cacheVersion, collector));
        return true;
    }

    private <O> boolean tryRegisterWriteMethod(
        DefaultCacheManager<O> cacheManager, Class<?> objClass, Method method,
        Class<?> cacheSupportClass, Object bean) {
        WriteMethodMeta writeMethodMeta = findWriteMethodMeta(method);
        if (writeMethodMeta == null) {
            return false;
        }

        DefaultWriteMethod<O> defaultWriteMethod = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            EvictObjectMeta evictObjectMeta = findEvictObjectMeta(parameter);
            if (evictObjectMeta != null) {
                // 使用@EvictObject
                if (!isParameterTypeOrMultiParameterType(parameter.getParameterizedType(), cacheSupportClass, objClass)) {
                    throw new CacheInitializationException("Parameter annotated with @CacheObject must be " +
                        "%s or List<%s> or %s[].", objClass.getName(), objClass.getName());
                }
                Function<Object, ?> paramAdapter = getParameterTypeOrMultiParameterTypeAdapter(parameter.getParameterizedType(), objClass);
                final int index = i;
                defaultWriteMethod = new DefaultWriteMethod<>(method, params ->
                    (List<O>) adaptList(paramAdapter.apply(params[index])));
                break;
            } else {
                EvictIndexMeta evictIndexMeta = findEvictIndexMeta(parameter);
                if (evictIndexMeta != null) {
                    // 使用@EvictIndex
                    String[] evictIndexValue = evictIndexMeta.getValue();
                    Class<?>[] evictIndexClasses = new Class[evictIndexValue.length];
                    List<Function<Object[], Object>> getIndexValueFuncList = new ArrayList<>();
                    for (int j = 0; j < evictIndexValue.length; j++) {
                        // 如果参数是列表或数组则PropertyPath作用于每个元素
                        Class<?> paramClass = parameter.getType();
                        PropertyPath propertyPath;
                        boolean multi;
                        if (Iterable.class.isAssignableFrom(paramClass)) {
                            propertyPath = new PropertyPath(ResolvableType.forType(parameter.getParameterizedType())
                                .as(Iterable.class).getGeneric(0).resolve(Object.class), evictIndexValue[j]);
                            multi = true;
                        } else if (paramClass.isArray()) {
                            propertyPath = new PropertyPath(paramClass.getComponentType(), evictIndexValue[j]);
                            multi = true;
                        } else {
                            propertyPath = new PropertyPath(parameter.getParameterizedType(), evictIndexValue[j]);
                            multi = false;
                        }

                        Class<?> evictIndexClass = propertyPath.getPropertyClass();

                        evictIndexClasses[j] = evictIndexClass;
                        final int index = i;
                        getIndexValueFuncList.add(params -> {
                            Object param = params[index];
                            if (multi) {
                                return adaptList(param, propertyPath::getPropertyValue);
                            } else {
                                return propertyPath.getPropertyValue(param);
                            }
                        });
                    }

                    // 获取指定的查询函数
                    String objQueryMethodName = writeMethodMeta.getObjQueryMethod();
                    Map<Integer, Integer> methodParamIdx2EvictIdxMap = new HashMap<>();
                    Map<Integer, Function<Object, ?>> methodParamIdx2ValueAdapterMap = new HashMap<>();
                    Method objQueryMethod = findObjQueryMethod(cacheSupportClass,
                        objQueryMethodName, objClass, evictIndexClasses,
                        methodParamIdx2EvictIdxMap, methodParamIdx2ValueAdapterMap);
                    if (objQueryMethod == null) {
                        log.error("Cannot find objQueryMethod in class, " +
                                "cacheSupportClass: {}, objQueryMethodName: {}, parameterTypes: {}",
                            cacheSupportClass, objQueryMethodName, evictIndexClasses);
                        throw new CacheInitializationException("Cannot find objQueryMethod '%s(%s)' in '%s'.",
                            objQueryMethodName, evictIndexClasses, cacheSupportClass.getName());
                    }

                    // 适配对象查询方法将其组装为DefaultWriteMethod
                    defaultWriteMethod = new DefaultWriteMethod<>(method, params -> {
                        int argCount = method.getParameterCount();
                        Object[] args = new Object[argCount];
                        for (int j = 0; j < argCount; j++) {
                            int evictIdx = methodParamIdx2EvictIdxMap.get(j);
                            Object obj = getIndexValueFuncList.get(evictIdx).apply(params);
                            Function<Object, ?> paramAdapter = methodParamIdx2ValueAdapterMap.get(j);
                            args[j] = paramAdapter.apply(obj);
                        }
                        return (List<O>) adaptList(ReflectionUtils.invokeMethod(objQueryMethod, bean, args));
                    });
                    break;
                }
            }
        }

        // 注册写方法
        if (defaultWriteMethod != null) {
            cacheManager.registerWriteMethod(defaultWriteMethod);
        }
        return true;
    }

    private Method findObjQueryMethod(Class<?> cacheSupportClass, String objQueryMethodName,
                                      Class<?> objClass, Class<?>[] evictIndexClasses,
                                      Map<Integer, Integer> methodParamIdx2EvictIdxMap,
                                      Map<Integer, Function<Object, ?>> methodParamIdx2ValueAdapterMap) {
        for (Method method : ReflectionUtils.getAllDeclaredMethods(cacheSupportClass)) {
            // 仅处理public方法，非公开方法可能会在Spring代理中出现null值情况
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            // 检查方法名称是否匹配
            if (!Objects.equals(method.getName(), objQueryMethodName)) {
                continue;
            }
            // 检查返回值是否匹配
            if (!isReturnTypeOrMultiReturnType(method.getGenericReturnType(), cacheSupportClass, objClass)) {
                continue;
            }
            // 检查所有参数是否匹配
            Parameter[] parameters = method.getParameters();
            if (parameters.length != evictIndexClasses.length) {
                continue;
            }
            // 检查所有方法参数与evictIndexClasses的匹配，需要完全匹配才能通过
            methodParamIdx2EvictIdxMap.clear();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Type parameterizedType = parameter.getParameterizedType();
                for (int j = 0; j < evictIndexClasses.length; j++) {
                    // 如果evictIndexClasses[j]还未被做为参数的匹配项则可以进行检查
                    if (!methodParamIdx2EvictIdxMap.containsValue(j)) {
                        // 检查evictIndexClasses[j]是否与当前参数匹配
                        Class<?> evictIndexClass = evictIndexClasses[j];
                        if (isParameterTypeOrMultiParameterType(parameterizedType, cacheSupportClass, evictIndexClass)) {
                            methodParamIdx2EvictIdxMap.put(i, j);
                            methodParamIdx2ValueAdapterMap.put(i, getParameterTypeOrMultiParameterTypeAdapter(parameterizedType, evictIndexClass));
                        }
                    }
                }
            }
            // 如果映射表中存放了evictIndexClasses.length个元素说明匹配成功
            if (methodParamIdx2EvictIdxMap.size() == evictIndexClasses.length) {
                return method;
            }
        }
        return null;
    }

    private void scanAndCollectListenKeyField(ResolvableType curType, List<DefaultListenKey> collector,
                                              Function<Object[], Object> getRootFunc, Class<?> objClass,
                                              Set<ResolvableType> visited) {
        Class<?> curClass = curType.resolve();
        if (curClass == null) {
            return;
        }

        // 遍历所有get方法和字段，检查@ListenKey注解，方法优先于字段
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(curClass);
        for (PropertyDescriptor pd : pds) {
            ListenKeyMeta listenKeyMeta = null;
            Function<Object[], Object> getValueFunc = null;
            ResolvableType nextType = null;
            // 优先检查get方法
            Method readMethod = pd.getReadMethod();
            if (readMethod != null) {
                nextType = ResolvableType.forMethodReturnType(readMethod);
                listenKeyMeta = findListenKeyMeta(readMethod);
                if (listenKeyMeta != null) {
                    getValueFunc = getRootFunc.andThen(root -> ReflectionUtils.invokeMethod(readMethod, root));
                }
            }
            // 如果没有从get方法中找到@ListenKey，则检查字段
            if (listenKeyMeta == null) {
                String pdName = pd.getName();
                Field field = ReflectionUtils.findField(curClass, pdName);
                if (field != null) {
                    nextType = ResolvableType.forField(field);
                    listenKeyMeta = findListenKeyMeta(readMethod);
                    if (listenKeyMeta != null) {
                        getValueFunc = getRootFunc.andThen(root -> {
                            ReflectionUtils.makeAccessible(field);
                            return ReflectionUtils.getField(field, root);
                        });
                    }
                }
            }

            if (listenKeyMeta != null) {
                // @ListenKey仅支持注释简单类型或简单的可迭代类型
                if (!isSimplePropertyOrIterableSimpleProperty(nextType)) {
                    log.error("Field annotated with @ListenKey only support simple types or collections of simple types, " +
                        "listenKeyMeta: {}, curType:{}, nextType: {}", listenKeyMeta, curType, nextType);
                    throw new CacheInitializationException(
                        "Field annotated with @ListenKey only support simple types or collections of simple types.");
                }
                // 如果找到了@ListenKey，则将其收集起来
                collector.add(new DefaultListenKey(
                    listenKeyMeta, getValueFunc, parseListenKeyExpr(objClass, listenKeyMeta.getValue()), nextType));
            } else if (nextType != null && isJavaBean(nextType.resolve())) {
                // 如果没有找到@ListenKey且当前字段类型是javaBean，则继续深入检查
                if (visited.add(nextType)) {
                    scanAndCollectListenKeyField(nextType, collector, getValueFunc, objClass, visited);
                    visited.remove(nextType);
                }
            }
        }
    }

    private boolean isSimplePropertyOrIterableSimpleProperty(ResolvableType type) {
        Class<?> clazz = type.resolve();
        if (clazz == null) {
            return false;
        } else if (isSimpleProperty(clazz)) {
            return true;
        } else if (Iterable.class.isAssignableFrom(clazz)
            && isSimpleProperty(type.as(Iterable.class).getGeneric(0).resolve())) {
            return true;
        } else if (clazz.isArray()
            && isSimpleProperty(clazz.getComponentType())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isJavaBean(Class<?> clazz) {
        if (clazz == null) {
            return false;
        } else if (clazz.isArray()) {
            return false;
        } else if (Iterator.class.isAssignableFrom(clazz)) {
            return false;
        } else if (Iterable.class.isAssignableFrom(clazz)) {
            return false;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return false;
        } else if (isSimpleProperty(clazz)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isSimpleProperty(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return BeanUtils.isSimpleProperty(clazz);
    }

    private PropertyPath parseListenKeyExpr(Class<?> objClass, String value) {
        PropertyPath propertyPath = new PropertyPath(objClass, value);

        if (!isSimpleProperty(propertyPath.getPropertyClass())) {
            log.error("Property is not simple type, propertyPath: {}", propertyPath);
            throw new CacheInitializationException("Property is not simple type.");
        }

        return propertyPath;
    }

    @Data
    static class CacheSupportClassFinder {

        private CacheSupportMeta cacheSupportMeta;
        private Class<?> cacheSupportClass;

        public void find(Object obj) {
            Class<?> clazz = AopUtils.getTargetClass(obj);
            if (Proxy.isProxyClass(clazz)) {
                // 处理mybatis代理
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    this.cacheSupportMeta = findCacheSupportMeta(clazz);
                    if (this.cacheSupportMeta != null) {
                        this.cacheSupportClass = anInterface;
                    }
                }
            } else {
                this.cacheSupportMeta = findCacheSupportMeta(clazz);
                if (this.cacheSupportMeta != null) {
                    this.cacheSupportClass = clazz;
                }
            }
        }

    }

}
