package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.expression.AbstractExpressionParser;
import fun.fengwk.convention4j.common.expression.ExpressionException;
import fun.fengwk.convention4j.common.lang.StringUtils;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class FragmentParser extends AbstractExpressionParser<InterpretContext> {

    private static final String OPEN1 = "#{";
    private static final String OPEN2 = "${";
    private static final String CLOSE = "}";

    private static final String PLACEHOLDER = "?";

    private final LinkedList<String> openStack = new LinkedList<>();

    @Override
    protected int isOpen(String str, int idx) {
        if (StringUtils.equals(str, idx, OPEN1, 0, OPEN1.length())) {
            openStack.push(OPEN1);
            return idx + OPEN1.length();
        }

        if (StringUtils.equals(str, idx, OPEN2, 0, OPEN2.length())) {
            openStack.push(OPEN2);
            return idx + OPEN2.length();
        }

        return -1;
    }

    @Override
    protected int isClose(String str, int idx) {
        if (!openStack.isEmpty() && StringUtils.equals(str, idx, CLOSE, 0, CLOSE.length())) {
            return idx + CLOSE.length();
        }

        return -1;
    }

    @Override
    protected String doParse(String expression, InterpretContext ctx) throws ExpressionException {
        Object val;
        try {
            val = Ognl.getValue(expression, ctx.getVarMap());
        } catch (OgnlException e) {
            throw new ExpressionException(e);
        }

        String open = openStack.pop();
        if (OPEN1.equals(open)) {
            ctx.getParamList().add(val);
            return PLACEHOLDER;
        } else {
            return String.valueOf(val);
        }
    }

}
