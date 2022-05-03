package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，双入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc2<P1, P2> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     */
    void apply(P1 p1, P2 p2);

}
