package fun.fengwk.convention4j.common.sql.dynamic.node;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.Objects;

/**
 * @author fengwk
 */
public class IfNode extends AbstractContainerNode {

    private final String test;

    public IfNode(String test) {
        this.test = test;
    }

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        if (isTrue(ctx)) {
            interpretChildren(ctx);
            return true;
        }

        return false;
    }

    private boolean isTrue(InterpretContext ctx) throws InterpretException {
        try {
            return Objects.equals(Ognl.getValue(test, ctx.getVarMap()), Boolean.TRUE);
        } catch (OgnlException ex) {
            throw new InterpretException(ex);
        }
    }

}
