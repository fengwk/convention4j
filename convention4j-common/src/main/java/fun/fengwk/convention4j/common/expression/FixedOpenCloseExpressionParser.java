package fun.fengwk.convention4j.common.expression;

import fun.fengwk.convention4j.common.StringUtils;

/**
 * @author fengwk
 */
public abstract class FixedOpenCloseExpressionParser<C> extends AbstractExpressionParser<C> {

    private final String open;
    private final String close;
    private int openCount;

    public FixedOpenCloseExpressionParser(String open, String close) {
        this.open = open;
        this.close = close;
    }

    @Override
    protected int isOpen(String str, int idx) {
        if (!StringUtils.equals(str, idx, open, 0, open.length())) {
            return -1;
        }

        openCount++;
        return idx + open.length();
    }

    @Override
    protected int isClose(String str, int idx) {
        if (openCount == 0 || !StringUtils.equals(str, idx, close, 0, close.length())) {
            return -1;
        }

        openCount--;
        return idx + close.length();
    }

}
