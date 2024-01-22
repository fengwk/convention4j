package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.common.ClassUtils;
import fun.fengwk.convention4j.common.reflect.GenericArrayTypeImpl;
import fun.fengwk.convention4j.common.reflect.ParameterizedTypeImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Slf4j
public class CacheInitializationUtils {

    private CacheInitializationUtils() {}

    public static boolean isReturnTypeOrMultiReturnType(Type returnType, Class<?> declaringClass, Class<?> targetClass) {
        returnType = resolveVariableType(returnType, declaringClass, Collections.emptyMap());
        ResolvableType rt = ResolvableType.forType(returnType);
        Class<?> returnClass = rt.resolve();
        if (returnClass == null) {
            return false;
        }
        if (ClassUtils.boxedIfPrimitiveType(returnClass) == ClassUtils.boxedIfPrimitiveType(targetClass)) {
            return true;
        }
        if (Iterable.class.isAssignableFrom(returnClass)
            && ClassUtils.boxedIfPrimitiveType(rt.as(Iterable.class).getGeneric(0).resolve()) == ClassUtils.boxedIfPrimitiveType(targetClass)) {
            return true;
        }
        if (returnClass.isArray()
            && returnClass.getComponentType().isAssignableFrom(targetClass)) {
            return true;
        }
        return false;
    }

    public static boolean isParameterTypeOrMultiParameterType(Type parameterType, Class<?> declaringClass, Class<?> classType) {
        parameterType = resolveVariableType(parameterType, declaringClass, Collections.emptyMap());
        ResolvableType rt = ResolvableType.forType(parameterType);
        Class<?> parameterClass = rt.resolve();
        if (parameterClass == null) {
            return false;
        }
        if (ClassUtils.boxedIfPrimitiveType(parameterClass) == ClassUtils.boxedIfPrimitiveType(classType)) {
            return true;
        }
        if (Iterable.class.isAssignableFrom(parameterClass)
            && ClassUtils.boxedIfPrimitiveType(rt.as(Iterable.class).getGeneric(0).resolve()) == ClassUtils.boxedIfPrimitiveType(classType)) {
            return true;
        }
        if (parameterClass.isArray()
            && ClassUtils.boxedIfPrimitiveType(classType).isAssignableFrom(ClassUtils.boxedIfPrimitiveType(parameterClass.getComponentType()))) {
            return true;
        }
        return false;
    }

    /**
     * @see #isParameterTypeOrMultiParameterType(Type, Class, Class)
     * @param parameterType
     * @param classType
     * @return
     */
    public static Function<Object, ?> getParameterTypeOrMultiParameterTypeAdapter(Type parameterType, Class<?> classType) {
        ResolvableType rt = ResolvableType.forType(parameterType);
        Class<?> parameterClass = rt.resolve();
        if (parameterClass != null) {
            if (Iterable.class.isAssignableFrom(parameterClass)) {
                return CacheInitializationUtils::adaptList;
            } else if (parameterClass.isArray()) {
                return o -> {
                    List<?> list = adaptList(o);
                    return list.toArray((Object[]) Array.newInstance(parameterClass));
                };
            }
        }
        return Function.identity();
    }

    public static List<?> adaptList(Object obj) {
        return adaptList(obj, Function.identity());
    }

    public static <T> List<T> adaptList(Object obj, Function<Object, T> mapper) {
        if (obj == null) {
            return Collections.emptyList();
        }

        if (obj instanceof Iterable) {
            List<T> result = new ArrayList<>();
            for (Object item : (Iterable<?>) obj) {
                result.add(mapper.apply(item));
            }
            return result;
        } else if (obj.getClass().isArray()) {
            List<T> result = new ArrayList<>();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                result.add(mapper.apply(Array.get(obj, i)));
            }
            return result;
        } else {
            return Collections.singletonList(mapper.apply(obj));
        }
    }

    public static Type resolveVariableType(Type type, Class<?> clazz, Map<Type, Type> variableTypeMap) {
        if (type == null) {
            return null;
        }
        if (variableTypeMap.containsKey(type)) {
            type = variableTypeMap.get(type);
        }
        if (type instanceof Class) {
            return type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            Type[] newActualTypeArguments = new Type[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                newActualTypeArguments[i] = resolveVariableType(actualTypeArguments[i], clazz, variableTypeMap);
            }
            Type newOwnerType = resolveVariableType(pt.getOwnerType(), clazz, variableTypeMap);
            Type newRawType = resolveVariableType(pt.getRawType(), clazz, variableTypeMap);
            return new ParameterizedTypeImpl(newActualTypeArguments, newOwnerType, newRawType);
        } else if (type instanceof GenericArrayType) {
            Type componentType = resolveVariableType(((GenericArrayType) type).getGenericComponentType(), clazz, variableTypeMap);
            return new GenericArrayTypeImpl(componentType);
        } else if (type instanceof TypeVariable) {
            GenericDeclaration gd = ((TypeVariable<?>) type).getGenericDeclaration();
            if (gd instanceof Class) {
                Class<?> baseClass = ((Class<?>) gd);
                TypeVariable<? extends Class<?>>[] baseTvs = baseClass.getTypeParameters();
                int foundIndex = 0;
                for (;foundIndex < baseTvs.length; foundIndex++) {
                    if (baseTvs[foundIndex].equals(type)) {
                        break;
                    }
                }
                return ResolvableType.forClass(clazz).as(baseClass).getGeneric(foundIndex)
                    .resolve(Object.class);
            } else {
                return Object.class;
            }
        } else if (type instanceof WildcardType) {
            Type upperBound = resolveBounds(((WildcardType) type).getUpperBounds());
            if (upperBound != null) {
                return resolveVariableType(upperBound, clazz, variableTypeMap);
            }
            Type lowerBound = resolveBounds(((WildcardType) type).getLowerBounds());
            return lowerBound == null ? Object.class : resolveVariableType(lowerBound, clazz, variableTypeMap);
        } else {
            throw new IllegalStateException(String.format("Unsupported resolve variable type '%s'", type));
        }
    }

    private static Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        // 默认使用第一个bound
        return bounds[0];
    }

}
