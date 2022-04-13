package fun.fengwk.convention4j.common.generic;

import org.junit.Test;

import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class TypeTokenTest {
    
    @SuppressWarnings("unused")
    private Map<String, String> map;

    @Test
    public void test1() throws NoSuchFieldException, SecurityException {
        assert new TypeToken<Map<String, String>>() {}.getType().equals(getClass().getDeclaredField("map").getGenericType());
    }
    
    @Test
    public void test2() throws NoSuchFieldException, SecurityException {
        assert new TypeToken<Map<String, String>>() {}.getType() == new TypeToken<Map<String, String>>() {}.getType();
    }
    
}
