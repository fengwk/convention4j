package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.function.VoidFunc2T1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwk
 */
public abstract class AbstractContainerNode extends AbstractNode {

    private final List<AbstractNode> children = new ArrayList<>();

    public List<AbstractNode> getChildren() {
        return children;
    }

    /**
     * 解释所有孩子节点，并且结合解析结果置入上下文中。
     *
     * @param ctx
     * @throws InterpretException
     */
    protected void interpretChildren(InterpretContext ctx) throws InterpretException {
        combine(ctx, (combineSqlBuilder, combineParamList) -> {
            for (AbstractNode node : children) {
                node.interpret(ctx);
                if (ctx.getSql() != null) {
                    combineSqlBuilder.append(' ').append(ctx.getSql());
                }
                if (ctx.getParamList() != null) {
                    combineParamList.addAll(ctx.getParamList());
                }
            }
        });
    }

    /**
     *
     * @param ctx
     * @param combineFunc 结合函数，用于将再当前容器节点处理当前上下文，需要将解释当前上下文的内容输入到StringBuilder和List中完成结合。
     * @throws InterpretException
     */
    protected void combine(InterpretContext ctx, VoidFunc2T1<StringBuilder, List<Object>, InterpretException> combineFunc) throws InterpretException {
        StringBuilder combineSqlBuilder = new StringBuilder();
        List<Object> combineParamList = new ArrayList<>();
        ctx.setSql(null);
        ctx.setParamList(null);

        combineFunc.apply(combineSqlBuilder, combineParamList);

        ctx.setSql(combineSqlBuilder.toString());
        ctx.setParamList(combineParamList);
    }

}
