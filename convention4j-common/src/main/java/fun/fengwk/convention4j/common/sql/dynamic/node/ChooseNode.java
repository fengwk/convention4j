package fun.fengwk.convention4j.common.sql.dynamic.node;

/**
 * @author fengwk
 */
public class ChooseNode extends AbstractContainerNode {

    private OtherwiseNode otherwise;

    public void setOtherwise(OtherwiseNode otherwise) {
        this.otherwise = otherwise;
    }

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        for (AbstractNode child : getChildren()) {
            if (child instanceof WhenNode) {
                if (child.interpret(ctx)) {
                    return true;
                }
            } else {
                throw new InterpretException(String.format("<choose> cannot contains %s", child));
            }
        }

        return otherwise != null && otherwise.interpret(ctx);
    }

}
