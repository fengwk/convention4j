package fun.fengwk.convention4j.common.expression;

import org.junit.jupiter.api.Test;

/**
 * @author fengwk
 */
public class FixedOpenCloseExpressionParserTest {

    @Test
    public void test() {
        String str = "qwe ${aa}, asd${qqq${www}eee}";
        FixedOpenCloseExpressionParser<Object> parser = new TestFixedOpenCloseExpressionParser(str);
        parser.parse(str);
    }

}
