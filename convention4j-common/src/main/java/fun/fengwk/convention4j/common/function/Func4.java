package fun.fengwk.convention4j.common.function;

/**
 * 4入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func4<R, P1, P2, P3, P4> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    R apply(P1 p1, P2 p2, P3 p3, P4 p4);

}
