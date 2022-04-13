package fun.fengwk.convention4j.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 
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
    
    private ClassUtils() {}
    
    /**
     * copy from spring
     * 
     * @return
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
    
    /**
     * 在被注解的元素上查找目标注解。
     * 
     * @param <A>
     * @param annotatedElement
     * @param annotationClass
     * @param includeAncestors
     * @return
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationClass, boolean includeAncestors) {
        if (includeAncestors) {
            return findAnnotationIncludeAncestors(annotatedElement.getAnnotations(), annotationClass);
        } else {
            return annotatedElement.getAnnotation(annotationClass);
        }
    }
    
    private static <A extends Annotation> A findAnnotationIncludeAncestors(Annotation[] annotations, Class<A> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                @SuppressWarnings("unchecked")
                A found = (A) annotation;
                return found;
            }
        }
        
        for (Annotation annotation : annotations) {
            A found = findAnnotationIncludeAncestors(annotation.annotationType().getAnnotations(), annotationClass);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * 如果是基本类型则进行装包。
     * 
     * @param type
     * @return
     */
    public static Type boxedIfPrimitiveType(Type type) {
        if (type instanceof Class) {
            Class<?> packClass = BOXED_MAP.get((Class<?>) type);
            if (packClass != null) {
                return packClass;
            }
        }
        
        return type;
    }
    
}