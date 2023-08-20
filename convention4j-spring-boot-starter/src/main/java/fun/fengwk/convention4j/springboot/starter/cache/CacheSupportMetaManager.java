package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.exception.CacheParseException;
import fun.fengwk.convention4j.springboot.starter.cache.meta.*;
import fun.fengwk.convention4j.springboot.starter.cache.support.CacheSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class CacheSupportMetaManager implements BeanPostProcessor {

    public static final String DEFAULT_VERSION = "";
    public static final int DEFAULT_EXPIRE_SECONDS = 60 * 60 * 24;
    private static final CacheConfigMeta DEFAULT_CACHE_CONFIG_META = new CacheConfigMeta(
        DEFAULT_VERSION, DEFAULT_EXPIRE_SECONDS);

    private final Map<Class<?>, CacheSupportMeta> supportMetaMap
        = Collections.synchronizedMap(new IdentityHashMap<>());

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CacheSupport) {
            parseMethodMetaInfo((CacheSupport<?, ?>) bean);
        }
        return bean;
    }

    /**
     * 根据仓储实例获取仓储元数据
     *
     * @param support 仓储实例
     * @return 仓储元数据
     */
    public CacheSupportMeta getSupportMeta(Object support) {
        Class<?> supportClass = getSupportClass(support);
        return supportMetaMap.get(supportClass);
    }

    private void parseMethodMetaInfo(CacheSupport<?, ?> support) {
        Class<?> supportClass = getSupportClass(support);

        // 类级别的缓存配置
        CacheConfig cacheConfig = AnnotationUtils.findAnnotation(supportClass, CacheConfig.class);

        // 获取data类型
        Class<?> dataClass = ResolvableType.forClass(supportClass)
            .as(CacheSupport.class).getGeneric(0).resolve();
        // 解析data类型元数据
        List<KeyMeta> dataKeyMetas = parseDataCacheKeyMetas(dataClass);

        // 构建仓储元数据
        CacheSupportMeta supportMeta = new CacheSupportMeta(support, dataClass, dataKeyMetas);

        // 遍历所有方法，根据注解解析方法元数据
        Method[] methods = supportClass.getDeclaredMethods();
        for (Method method : methods) {
            CacheReadMethod cacheReadMethod = AnnotationUtils.findAnnotation(method, CacheReadMethod.class);
            if (cacheReadMethod != null) {
                checkAopMethod(method, CacheReadMethod.class.getSimpleName());
                parseReadMethodCacheMeta(supportMeta, method, cacheConfig, cacheReadMethod);
            } else {
                CacheWriteMethod cacheWriteMethod = AnnotationUtils.findAnnotation(method, CacheWriteMethod.class);
                if (cacheWriteMethod != null) {
                    checkAopMethod(method, CacheWriteMethod.class.getSimpleName());
                    parseWriteMethodCacheMeta(supportMeta, method, cacheConfig);
                }
            }
        }

        // 检查仓储信息
        supportMeta.check();
        // 缓存仓储元数据
        supportMetaMap.put(supportClass, supportMeta);
    }

    private Class<?> getSupportClass(Object obj) {
        Class<?> clazz = AopUtils.getTargetClass(obj);
        // 处理mybatis代理
        if (Proxy.isProxyClass(clazz)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (CacheSupport.class.isAssignableFrom(anInterface)) {
                    clazz = anInterface;
                    break;
                }
            }
        }
        return clazz;
    }

    private List<KeyMeta> parseDataCacheKeyMetas(Class<?> dataClass) {
        if (!isJavaBean(dataClass)) {
            throw new CacheParseException("Data type must be a java bean: " + dataClass);
        }

        // 扫描data类型中被@Key注解的字段
        List<KeyMeta> dataKeyMeta = new ArrayList<>();
        scanKeyMeta(dataKeyMeta, (anno, pName, valueGetter) ->
            new KeyMeta(false, anno.value().isEmpty() ? pName : anno.value(), valueGetter),
            Key.class, new HashSet<>(), ResolvableType.forClass(dataClass), o -> o);

        // 扫描data类型中被@IdKey注解的字段
        List<KeyMeta> dataIdKeyMeta = new ArrayList<>();
        scanKeyMeta(dataIdKeyMeta, (anno, pName, valueGetter) ->
                new KeyMeta(true, anno.value().isEmpty() ? pName : anno.value(), valueGetter),
            IdKey.class, new HashSet<>(), ResolvableType.forClass(dataClass), o -> o);

        return KeyMeta.merge(dataIdKeyMeta, dataKeyMeta);
    }

    private void parseReadMethodCacheMeta(
            CacheSupportMeta supportMeta, Method method, CacheConfig cacheConfig, CacheReadMethod cacheReadMethod) {
        CacheConfigMeta cacheConfigMeta = cacheConfig == null ? DEFAULT_CACHE_CONFIG_META :
            new CacheConfigMeta(cacheConfig.version(), cacheConfig.expireSeconds());

        List<MethodKeyMeta> keyMetas = new ArrayList<>();
        scanParameters(keyMetas, method,
            (anno, pName, valueGetter, parameterIndex, multi) ->
                new MethodKeyMeta(false, anno.value().isEmpty() ? pName : anno.value(), valueGetter,
                    parameterIndex, multi), Key.class);

        List<MethodKeyMeta> idKeyMetas = new ArrayList<>();
        scanParameters(idKeyMetas, method,
            (anno, pName, valueGetter, parameterIndex, multi) ->
                new MethodKeyMeta(true, anno.value().isEmpty() ? pName : anno.value(), valueGetter,
                    parameterIndex, multi), IdKey.class);

        ResolvableType retType = ResolvableType.forMethodReturnType(method);

        supportMeta.addCacheReadMethodMeta(new CacheReadMethodMeta(
            method, cacheConfigMeta, KeyMeta.merge(idKeyMetas, keyMetas), retType,
            supportMeta.getDataClass(), cacheReadMethod.useIdQuery()));
    }

    private void parseWriteMethodCacheMeta(
            CacheSupportMeta supportMeta, Method method, CacheConfig cacheConfig) {
        CacheConfigMeta cacheConfigMeta = cacheConfig == null ? DEFAULT_CACHE_CONFIG_META :
            new CacheConfigMeta(cacheConfig.version(), cacheConfig.expireSeconds());

        List<MethodKeyMeta> idKeyMetas = new ArrayList<>();
        scanParameters(idKeyMetas, method,
            (anno, pName, valueGetter, parameterIndex, multi) ->
                new MethodKeyMeta(true, anno.value().isEmpty() ? pName : anno.value(), valueGetter,
                    parameterIndex, multi), IdKey.class);

        supportMeta.addCacheWriteMethodMeta(new CacheWriteMethodMeta(method, cacheConfigMeta, idKeyMetas));
    }

    private void checkAopMethod(Method method, String cacheName) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new CacheParseException(
                cacheName + " annotation can not be used on static method: " + method);
        }
        if (!Modifier.isPublic(modifiers)) {
            throw new CacheParseException(
                cacheName + " annotation can not be used on non-public method: " + method);
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

    private ResolvableType unwrap(ResolvableType type) {
        if (type.isArray()) {
            return unwrap(type.getComponentType());
        } else if (Iterable.class.isAssignableFrom(type.toClass())) {
            return unwrap(type.getGeneric(0));
        } else {
            return type;
        }
    }

    private <M extends KeyMeta, ANNO extends Annotation> void scanParameters(
        List<M> keyMetas, Method method,
        ParameterCacheKeyMetaBuilder<M, ANNO> metaBuilder, Class<ANNO> scanAnnotationClass) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            ResolvableType parameterType = ResolvableType.forMethodParameter(method, i);
            Class<?> parameterClass = parameterType.resolve();
            boolean multi = false;
            if (parameterClass != null && !isSimpleProperty(parameterClass) && !isJavaBean(parameterClass)) {
                parameterType = unwrap(parameterType);
                parameterClass = parameterType.resolve();
                multi = true;
            }
            if (parameterClass != null && !isSimpleProperty(parameterClass) && !isJavaBean(parameterClass)) {
                continue;
            }

            Parameter parameter = parameters[i];
            ANNO anno = AnnotationUtils.findAnnotation(parameter, scanAnnotationClass);
            if (anno != null) {
                M meta = metaBuilder.build(anno, parameter.getName(), o -> o, i, multi);
                keyMetas.add(meta);
            } else {
                final int finalIndex = i;
                final boolean finalMulti = multi;
                scanKeyMeta(keyMetas,
                    (a, pn, valueGetter) -> metaBuilder.build(a, pn, valueGetter, finalIndex, finalMulti),
                    scanAnnotationClass,
                    new HashSet<>(), parameterType, o -> o);
            }
        }
    }

    private <M extends KeyMeta, ANNO extends Annotation> void scanKeyMeta(
        List<M> keyMetas, CacheKeyMetaBuilder<M, ANNO> metaBuilder, Class<ANNO> scanAnnotationClass,
        Set<Class<?>> visited, ResolvableType beanType,
        Function<Object, Object> currentBeanGetter) {
        if (!isJavaBean(beanType.resolve())) {
            return;
        }
        Class<?> beanClass = beanType.toClass();
        if (!visited.add(beanClass)) {
            return;
        }

        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor pd : pds) {
            Method readMethod = pd.getReadMethod();
            ANNO anno = AnnotationUtils.findAnnotation(readMethod, scanAnnotationClass);
            if (anno == null) {
                String pdName = pd.getName();
                Field field = ReflectionUtils.findField(beanClass, pdName);
                if (field != null) {
                    anno = AnnotationUtils.findAnnotation(field, scanAnnotationClass);
                }
            }

            Function<Object, Object> keyGetter = currentBeanGetter
                .andThen(o -> o == null ? null : ReflectionUtils.invokeMethod(readMethod, o));

            if (anno != null) {
                M meta = metaBuilder.build(anno, pd.getName(), keyGetter);
                keyMetas.add(meta);
            } else {
                scanKeyMeta(keyMetas, metaBuilder, scanAnnotationClass,
                    visited, ResolvableType.forMethodReturnType(readMethod), keyGetter);
            }
        }
    }

    @FunctionalInterface
    interface CacheKeyMetaBuilder<M extends KeyMeta, ANNO extends Annotation> {

        M build(ANNO anno, String propertyName, Function<Object, Object> valueGetter);

    }

    @FunctionalInterface
    interface ParameterCacheKeyMetaBuilder<M extends KeyMeta, ANNO extends Annotation> {

        M build(ANNO anno, String propertyName, Function<Object, Object> valueGetter,
                int parameterIndex, boolean multi);

    }

}
