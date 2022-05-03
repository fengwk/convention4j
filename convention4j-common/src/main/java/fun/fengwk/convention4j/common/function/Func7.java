package fun.fengwk.convention4j.common.function;

/**
 * 7入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func7<R, P1, P2, P3, P4, P5, P6, P7> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param p7
     * @return
     */
    R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

}
