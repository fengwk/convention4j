package fun.fengwk.convention4j.common.function;

/**
 * 3入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func3<R, P1, P2, P3> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    R apply(P1 p1, P2 p2, P3 p3);

}
