package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.expression.ExpressionException;

import java.util.ArrayList;

/**
 * @author fengwk
 */
public class FragmentNode extends AbstractNode {

    private final String fragment;

    public FragmentNode(String fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        try {
            ctx.setParamList(new ArrayList<>());
            ctx.setSql(new FragmentParser().parse(fragment, ctx));
        } catch (ExpressionException e) {
            throw new InterpretException(e);
        }
        return true;
    }

}
