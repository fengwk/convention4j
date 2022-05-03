package fun.fengwk.convention4j.common.expression;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * @author fengwk
 */
public class OgnlExpressionParser<C> extends FixedOpenCloseExpressionParser<C> {

    private static final String DEFAULT_OPEN = "${";
    private static final String DEFAULT_CLOSE = "}";

    /**
     * 使用默认的open和close构建解析器，默认为{@code ${}}。
     */
    public OgnlExpressionParser() {
        this(DEFAULT_OPEN, DEFAULT_CLOSE);
    }

    /**
     * 指定open和close构造解析器。
     *
     * @param open
     * @param close
     */
    public OgnlExpressionParser(String open, String close) {
        super(open, close);
    }

    @Override
    protected String doParse(String expression, C ctx) throws ExpressionException {
        try {
            return String.valueOf(Ognl.getValue(expression, ctx));
        } catch (OgnlException e) {
            throw new ExpressionException(e);
        }
    }

}
