package fun.fengwk.convention4j.common.sql.dynamic.node;

/**
 * @author fengwk
 */
public abstract class AbstractNode {

    /**
     * 通过参数上下文解释sql语句。
     *
     * @param ctx not null
     * @return 如果当前节点支持该上下文的解释返回true，否则返回false。
     */
    public abstract boolean interpret(InterpretContext ctx) throws InterpretException;

}