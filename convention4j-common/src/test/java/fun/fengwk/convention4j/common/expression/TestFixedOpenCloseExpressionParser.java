package fun.fengwk.convention4j.common.expression;

import org.junit.jupiter.api.Assertions;

/**
 * @author fengwk
 */
public class TestFixedOpenCloseExpressionParser extends FixedOpenCloseExpressionParser<Object> {

    private final String str;

    public TestFixedOpenCloseExpressionParser(String str) {
        super("${", "}");
        this.str = str;
    }

    @Override
    protected String doParse(String expression, Object ctx, int lo, int hi) throws ExpressionException {
        System.out.println(str.substring(lo, hi));
        Assertions.assertEquals(expression, str.substring(lo, hi));
        return "${" + expression + "}";
    }

}
