package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.common.cache.exception.CacheInitializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Function;

import static fun.fengwk.convention4j.springboot.starter.cache.registry.CacheInitializationUtils.*;

/**
 * Java bean property path.
 *
 * @author fengwk
 */
@Slf4j
public class PropertyPath {

    private static final String SEPARATOR = ".";

    private final Class<?> beanClass;
    private final String path;
    private final Class<?> propertyClass;
    private final Function<Object, ?> getPropertyFunction;

    /**
     *
     * @param beanClass bean对象
     * @param path property1.property2.property3格式，如果为空表示beanClass本身
     */
    public PropertyPath(Class<?> beanClass, String path) {
        Function<Object, ?> getPropertyFunction = Function.identity();
        Class<?> curClass = beanClass;
        StringTokenizer st = new StringTokenizer(path, SEPARATOR);
        while (st.hasMoreTokens()) {
            if (curClass == null) {
                log.error("Cannot resolve property path, beanClass: {}, path: {}", beanClass, path);
                throw new CacheInitializationException("Cannot resolve property '%s' in '%s'.", path, beanClass);
            }
            String propertyName = st.nextToken();
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(curClass, propertyName);
            if (pd == null) {
                log.error("Cannot find propertyName in class, curClass: {}, propertyName: {}", curClass, propertyName);
                throw new IllegalArgumentException("Cannot find '" + propertyName + "' in '" + curClass + "'.");
            }
            Method readMethod = pd.getReadMethod();
            if (readMethod != null) {
                getPropertyFunction = getPropertyFunction.andThen(root -> ReflectionUtils.invokeMethod(readMethod, root));
                Type resolevedType = resolveVariableType(readMethod.getGenericReturnType(), curClass);
                curClass = ResolvableType.forType(resolevedType).resolve();
            } else {
                String pdName = pd.getName();
                Field field = ReflectionUtils.findField(curClass, pdName);
                if (field == null) {
                    log.error("Cannot find propertyName in class, curClass: {}, propertyName: {}", curClass, propertyName);
                    throw new IllegalArgumentException("Cannot find '" + propertyName + "' in '" + curClass + "'.");
                }
                getPropertyFunction = getPropertyFunction.andThen(root -> {
                    ReflectionUtils.makeAccessible(field);
                    return ReflectionUtils.getField(field, root);
                });
                Type resolvedType = resolveVariableType(field.getGenericType(), curClass);
                curClass = ResolvableType.forType(resolvedType).resolve();
            }
        }

        this.beanClass = beanClass;
        this.path = path;
        this.propertyClass = curClass;
        this.getPropertyFunction = getPropertyFunction;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public Object getPropertyValue(Object bean) {
        return bean == null ? null : getPropertyFunction.apply(bean);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyPath that = (PropertyPath) o;
        return Objects.equals(beanClass, that.beanClass) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass, path);
    }

    @Override
    public String toString() {
        return "PropertyPath{" +
            "beanClass=" + beanClass +
            ", path='" + path + '\'' +
            ", propertyClass=" + propertyClass +
            ", getPropertyFunction=" + getPropertyFunction +
            '}';
    }

}
