package fun.fengwk.convention4j.common.expression;

import java.util.LinkedList;

/**
 * 抽象的表达式解析器。
 *
 * @author fengwk
 */
public abstract class AbstractExpressionParser<C> {

    /**
     * 解析str中包含的表达式。
     *
     * @param str
     * @return
     */
    public String parse(String str) throws ExpressionException {
        return parse(str, null);
    }

    /**
     * 解析str中包含的表达式。
     *
     * @param str
     * @param ctx
     * @return
     * @throws ExpressionException 表达式解析出现错误时抛出该异常。
     */
    public String parse(String str, C ctx) throws ExpressionException {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // stack元素为出现OPEN之后的首个位置
        LinkedList<Integer> stack = new LinkedList<>();

        // 使用栈顺序为每个${...}区间维护一个新的StringBuilder
        LinkedList<StringBuilder> sbStack = new LinkedList<>();
        sbStack.push(new StringBuilder());

        for (int i = 0; i < str.length();) {
            int j;
            if ((j = isOpen(str, i)) != -1) {
                i = j;
                stack.push(j);
                sbStack.push(new StringBuilder());

            } else if ((j = isClose(str, i)) != -1) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException(
                            String.format("Expression error, cannot found open for close in pos %d", i));
                }

                String expression = sbStack.pop().toString();

                if (stack.isEmpty()) {
                    throw new IllegalArgumentException(
                            String.format("Expression error, cannot found open for close in pos %d", i));
                }

                String parsed = doParse(expression, ctx);
                StringBuilder peek = sbStack.peek();
                assert peek != null;
                peek.append(parsed);
                i = j;

            } else {
                StringBuilder peek = sbStack.peek();
                assert peek != null;
                peek.append(str.charAt(i));
                i++;
            }
        }

        StringBuilder peek = sbStack.peek();
        assert peek != null;
        return peek.toString();
    }

    /**
     * 检查str以idx为起始的位置是否是表达式的开始，如果是返回表达式内部的起始位置，否则返回-1。
     *
     * @param str
     * @param idx
     * @return
     */
    protected abstract int isOpen(String str, int idx);

    /**
     * 检查str以idx为起始的位置是否是表达式的结束，如果是返回表达式结束后的起始位置，否则返回-1。
     *
     * @param str
     * @param idx
     * @return
     */
    protected abstract int isClose(String str, int idx);

    /**
     * 处理表达式解析，返回解析后的结果。
     *
     * @param expression
     * @param ctx
     * @return
     */
    protected abstract String doParse(String expression, C ctx) throws ExpressionException;

}
