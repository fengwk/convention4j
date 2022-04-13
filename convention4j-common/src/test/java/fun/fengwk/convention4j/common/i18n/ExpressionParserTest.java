package fun.fengwk.convention4j.common.i18n;

import ognl.OgnlException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class ExpressionParserTest {

    @Test
    public void test1() throws OgnlException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", "fengwk");
        ctx.put("age", 18);
        
        String str = ExpressionParser.parse("${name} is ${age} years old", ctx);
        assert str.equals("fengwk is 18 years old");
    }
    
    @Test
    public void test2() throws OgnlException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", "fengwk");
        ctx.put("a", 2);
        ctx.put("b", 5);
        ctx.put("c", 4);
        
        String str = ExpressionParser.parse("${name} is ${a * ${b + ${c}}} years old", ctx);
        assert str.equals("fengwk is 18 years old");
    }
    
    @Test
    public void test3() throws OgnlException {
        String str = ExpressionParser.parse("${name == null ? 'is null' : name}", Collections.emptyMap());
        assert str.equals("is null");
    }
    
}
