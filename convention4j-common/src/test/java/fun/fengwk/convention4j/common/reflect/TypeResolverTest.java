package fun.fengwk.convention4j.common.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class TypeResolverTest {
    
    @Test
    public void test1() {
        ParameterizedType pt = new TypeResolver(C.class)
                .as(G.class)
                .asParameterizedType();
        assert pt.getActualTypeArguments()[0].toString().equals("java.util.List<java.lang.Integer>");
        assert pt.getActualTypeArguments()[1].toString().equals("java.util.Map<java.util.Map<java.lang.String, java.lang.Long>, ?>");
        assert pt.getActualTypeArguments()[2].toString().equals("fun.fengwk.convention4j.common.reflect.TypeResolverTest.fun.fengwk.convention4j.common.reflect.TypeResolverTest$P<java.lang.Integer, java.util.Map<java.lang.String, java.lang.Long>, java.lang.Character>");
    }
    
    @Test
    public void test2() {
        ParameterizedType pt = new TypeResolver(C.class)
                .as(P.class)
                .asParameterizedType();
        assert pt.getActualTypeArguments()[0].toString().equals("class java.lang.Integer");
        assert pt.getActualTypeArguments()[1].toString().equals("java.util.Map<java.lang.String, java.lang.Long>");
        assert pt.getActualTypeArguments()[2].toString().equals("class java.lang.Character");
    }
    
    @Test
    public void test3() throws NoSuchFieldException, SecurityException {
        TypeResolver tr = new TypeResolver(TestClass.class.getDeclaredField("map").getGenericType());
        Type t1 = tr.asParameterizedType().getActualTypeArguments()[1];
        
        assert new TypeResolver(t1).as(Map.class).asParameterizedType().getActualTypeArguments()[0].toString().equals("E");
    }
    
    class G<V1, V2, V3 extends G<V1, V2, V3>> {}
    
    class P<V1, V2, V3> extends G<List<V1>, Map<V2, ?>, P<V1, V2, V3>> {}
    
    class C extends P<Integer, Map<String, Long>, Character> {}
    
}
