package fun.fengwk.convention4j.common.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fengwk
 */
public class ClassUtils {

    /**
     * 基本类型装包映射。
     */
    private static final Map<Class<?>, Class<?>> BOXED_MAP;

    static {
        Map<Class<?>, Class<?>> boxedMap = new HashMap<>();
        boxedMap.put(byte.class, Byte.class);
        boxedMap.put(short.class, Short.class);
        boxedMap.put(int.class, Integer.class);
        boxedMap.put(long.class, Long.class);
        boxedMap.put(float.class, Float.class);
        boxedMap.put(double.class, Double.class);
        boxedMap.put(char.class, Character.class);
        boxedMap.put(boolean.class, Boolean.class);
        BOXED_MAP = boxedMap;
    }

    private ClassUtils() {
    }

    /**
     * copy from spring
     *
     * @return
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * 在被注解的元素上查找目标注解（包含继承的注解）
     *
     * @param annotatedElement 被注解标记的元素。
     * @param annotationClass  注解的Class。
     * @param <A>              注解。
     * @return 首个找到的注解。
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                          Class<A> annotationClass) {
        return findAnnotation(annotatedElement, annotationClass, true);
    }

    /**
     * 在被注解的元素上查找目标注解。
     *
     * @param annotatedElement 被注解标记的元素。
     * @param annotationClass  注解的Class。
     * @param includeAncestors 是否需要向注解的注解标记寻找，可以实现类似注解的继承关系功能。
     * @param <A>              注解。
     * @return 首个找到的注解。
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                          Class<A> annotationClass, boolean includeAncestors) {
        if (includeAncestors) {
            return doFindAnnotation(annotatedElement, annotationClass, new HashSet<>());
        } else {
            return annotatedElement.getAnnotation(annotationClass);
        }
    }

    private static <A extends Annotation> A doFindAnnotation(AnnotatedElement annotatedElement,
                                                             Class<A> annotationType,
                                                             Set<Class<? extends Annotation>> visited) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (visited.add(annotation.annotationType())) {
                if (annotation.annotationType() == annotationType) {
                    return annotationType.cast(annotation);
                }
                A superAnno = doFindAnnotation(annotation.annotationType(), annotationType, visited);
                if (superAnno != null) {
                    return superAnno;
                }
            }
        }
        return null;
    }

    /**
     * 如果是基本类型则进行装包。
     *
     * @param clazz clazz
     * @return 解包后的类型
     */
    public static Class<?> boxedIfPrimitiveType(Class<?> clazz) {
        Class<?> packClass = BOXED_MAP.get(clazz);
        if (packClass != null) {
            return packClass;
        }
        return clazz;
    }

    /**
     * 获取所有定义的字段（包含父类中继承的字段）
     *
     * @param clazz clazz
     * @return 所有字段
     */
    public static Set<Field> getAllDeclaredFields(Class<?> clazz) {
        return getAllDeclaredFields(clazz, true);
    }

    /**
     * 获取所有定义的字段
     *
     * @param clazz            clazz
     * @param includeAncestors 是否要包含父类中继承的字段
     * @return 所有字段
     */
    public static Set<Field> getAllDeclaredFields(Class<?> clazz, boolean includeAncestors) {
        Field[] declaredFields = clazz.getDeclaredFields();
        Set<Field> allDeclaredFields = new HashSet<>(Arrays.asList(declaredFields));
        if (!includeAncestors) {
            return allDeclaredFields;
        }

        clazz = clazz.getSuperclass();
        while (clazz != null) {
            declaredFields = clazz.getDeclaredFields();
            allDeclaredFields.addAll(Arrays.asList(declaredFields));
            clazz = clazz.getSuperclass();
        }
        return allDeclaredFields;
    }

    /**
     * 获取所有定义的方法（包含父类中继承的方法）
     *
     * @param clazz clazz
     * @return 所有方法
     */
    public static Set<Method> getAllDeclaredMethods(Class<?> clazz) {
        return getAllDeclaredMethods(clazz, true);
    }

    /**
     * 获取所有定义的方法
     *
     * @param clazz            clazz
     * @param includeAncestors 是否要包含父类中继承的方法
     * @return 所有方法
     */
    public static Set<Method> getAllDeclaredMethods(Class<?> clazz, boolean includeAncestors) {
        Method[] declaredFields = clazz.getDeclaredMethods();
        Set<Method> allDeclaredMethods = new HashSet<>(Arrays.asList(declaredFields));
        if (!includeAncestors) {
            return allDeclaredMethods;
        }

        clazz = clazz.getSuperclass();
        while (clazz != null) {
            declaredFields = clazz.getDeclaredMethods();
            allDeclaredMethods.addAll(Arrays.asList(declaredFields));
            clazz = clazz.getSuperclass();
        }
        return allDeclaredMethods;
    }

}
