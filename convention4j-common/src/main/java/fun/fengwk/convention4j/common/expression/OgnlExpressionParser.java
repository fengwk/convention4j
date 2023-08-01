package fun.fengwk.convention4j.common.expression;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.Objects;

/**
 * @author fengwk
 */
public class OgnlExpressionParser<C> extends FixedOpenCloseExpressionParser<C> {

    private static final String DEFAULT_OPEN = "${";
    private static final String DEFAULT_CLOSE = "}";
    private static final String DEFAULT_NULL_VALUE = "";

    private final String nullValue;

    /**
     * 使用默认的open和close构建解析器，默认为{@code ${}}。
     */
    public OgnlExpressionParser() {
        this(DEFAULT_OPEN, DEFAULT_CLOSE, DEFAULT_NULL_VALUE);
    }

    /**
     *
     * @param nullValue
     */
    public OgnlExpressionParser(String nullValue) {
        this(DEFAULT_OPEN, DEFAULT_CLOSE, nullValue);
    }

    /**
     *
     * @param open
     * @param close
     * @param nullValue
     */
    public OgnlExpressionParser(String open, String close, String nullValue) {
        super(open, close);
        this.nullValue = nullValue;
    }

    @Override
    protected String doParse(String expression, C ctx) throws ExpressionException {
        try {
            return Objects.toString(Ognl.getValue(expression, ctx), nullValue);
        } catch (OgnlException e) {
            throw new ExpressionException(e);
        }
    }

}
