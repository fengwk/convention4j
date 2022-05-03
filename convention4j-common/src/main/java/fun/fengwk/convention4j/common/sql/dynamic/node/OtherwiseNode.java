package fun.fengwk.convention4j.common.sql.dynamic.node;

/**
 * @author fengwk
 */
public class OtherwiseNode extends AbstractContainerNode {

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        interpretChildren(ctx);
        return true;
    }

}
