package fun.fengwk.convention4j.common;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author fengwk
 */
public class ClassUtilsTest {
    
    @Test
    public void testGetDefaultClassLoader() {
        assert ClassUtils.getDefaultClassLoader() != null;
    }
    
    @Test
    public void testFindAnnotation1() {
        assert ClassUtils.findAnnotation(ParentTest.class, Parent.class, true).annotationType() == Parent.class;
    }
    
    @Test
    public void testFindAnnotation2() {
        assert ClassUtils.findAnnotation(ChildTest.class, Parent.class, true).annotationType() == Parent.class;
    }
    
    @Test
    public void testFindAnnotation3() {
        assert ClassUtils.findAnnotation(ChildTest.class, Parent.class, false) == null;
    }
    
    @Test
    public void testFindAnnotation4() {
        assert ClassUtils.findAnnotation(ChildTest.class, Child.class, false).annotationType() == Child.class;
    }
    
    @Test
    public void testPackIfPrimitiveType1() {
        assert ClassUtils.boxedIfPrimitiveType(byte.class) == Byte.class;
    }
    
    @Test
    public void testPackIfPrimitiveType2() {
        assert ClassUtils.boxedIfPrimitiveType(short.class) == Short.class;
    }
    
    @Test
    public void testPackIfPrimitiveType3() {
        assert ClassUtils.boxedIfPrimitiveType(int.class) == Integer.class;
    }
    
    @Test
    public void testPackIfPrimitiveType4() {
        assert ClassUtils.boxedIfPrimitiveType(long.class) == Long.class;
    }
    
    @Test
    public void testPackIfPrimitiveType5() {
        assert ClassUtils.boxedIfPrimitiveType(float.class) == Float.class;
    }
    
    @Test
    public void testPackIfPrimitiveType6() {
        assert ClassUtils.boxedIfPrimitiveType(double.class) == Double.class;
    }
    
    @Test
    public void testPackIfPrimitiveType7() {
        assert ClassUtils.boxedIfPrimitiveType(char.class) == Character.class;
    }
    
    @Test
    public void testPackIfPrimitiveType8() {
        assert ClassUtils.boxedIfPrimitiveType(boolean.class) == Boolean.class;
    }
    
    @Test
    public void testPackIfPrimitiveType9() {
        assert ClassUtils.boxedIfPrimitiveType(Object.class) == Object.class;
    }
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Parent {}
    
    @Parent
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Child {}
    
    @Parent
    class ParentTest {}
    
    @Child
    class ChildTest {}
    
    interface Methods {
        
        default boolean returnTrue() {
            return true;
        }
        
    }
    
    
}
