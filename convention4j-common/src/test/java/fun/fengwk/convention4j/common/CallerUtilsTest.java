package fun.fengwk.convention4j.common;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class CallerUtilsTest {

    @Test
    public void test1() {
        test1Call();
    }
    
    private void test1Call() {
        Class<?> callerClass = CallerUtils.getCallerClass(0);
        assert CallerUtilsTest.class == callerClass;
    }
    
    @Test
    public void test2() {
        test2Call(null, 0, null);
    }
    
    private void test2Call(String a, int b, List<String> c) {
        Method callerMethod = CallerUtils.getCallerMethod(0, String.class, int.class, List.class);
        assert callerMethod != null && callerMethod.getName().equals("test2Call");
    }
    
    @Test
    public void test3() {
        test3Call(null, 0, null);
    }
    
    private static void test3Call(String a, int b, List<String> c) {
        Method callerMethod = CallerUtils.getCallerMethod(0, String.class, int.class, List.class);
        assert callerMethod != null && callerMethod.getName().equals("test3Call");
    }
    
    @Test
    public void test4() {
        test4Call(null, 0, null);
    }
    
    private static void test4Call(String a, int b, List<String> c) {
        test4CallCall();
    }
    
    private static void test4CallCall() {
        Method callerMethod = CallerUtils.getCallerMethod(1, String.class, int.class, List.class);
        assert callerMethod != null && callerMethod.getName().equals("test4Call");
    }
    
    @Test
    public void test5() throws NoSuchMethodException, SecurityException {
        new C(null, 0, null);
    }
    
    static class C {
        
        public C(String a, int b, List<String> c) throws NoSuchMethodException, SecurityException {
            Constructor<?> callerConstructor = CallerUtils.getCallerConstructor(0, String.class, int.class, List.class);
            assert C.class.getConstructor(String.class, int.class, List.class).equals(callerConstructor);
        }
        
    }
    
}
