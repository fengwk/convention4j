package fun.fengwk.convention4j.common.function;

/**
 * 双入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func2<R, P1, P2> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @return
     */
    R apply(P1 p1, P2 p2);

}
