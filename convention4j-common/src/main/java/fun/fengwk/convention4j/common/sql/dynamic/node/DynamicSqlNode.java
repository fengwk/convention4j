package fun.fengwk.convention4j.common.sql.dynamic.node;

/**
 * 多套sql根节点。
 *
 * @author fengwk
 */
public class DynamicSqlNode extends AbstractContainerNode {

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        interpretChildren(ctx);
        return true;
    }

}
