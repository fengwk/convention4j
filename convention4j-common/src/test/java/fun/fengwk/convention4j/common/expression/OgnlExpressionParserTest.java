package fun.fengwk.convention4j.common.expression;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class OgnlExpressionParserTest {

    @Test
    public void test1() throws ExpressionException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", "fengwk");
        ctx.put("age", 18);
        
        String str = new OgnlExpressionParser<>().parse("${name} is ${age} years old", ctx);
        assert str.equals("fengwk is 18 years old");
    }
    
    @Test
    public void test2() throws ExpressionException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", "fengwk");
        ctx.put("a", 2);
        ctx.put("b", 5);
        ctx.put("c", 4);
        
        String str = new OgnlExpressionParser<>().parse("${name} is ${a * ${b + ${c}}} years old", ctx);
        assert str.equals("fengwk is 18 years old");
    }
    
    @Test
    public void test3() throws ExpressionException {
        String str = new OgnlExpressionParser<>().parse("${name == null ? 'is null' : name}", Collections.emptyMap());
        assert str.equals("is null");
    }

    @Test
    public void test4() throws ExpressionException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("age", 18);

        String str = new OgnlExpressionParser<>().parse("${name} is ${age} years old", ctx);
        assert str.equals("null is 18 years old");
    }

    @Test
    public void test5() throws ExpressionException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("age", 18);

        String str = new OgnlExpressionParser<>().parse("name} is ${age} years old", ctx);
        assert str.equals("name} is 18 years old");
    }

}
