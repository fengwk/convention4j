package fun.fengwk.convention4j.common.function;

/**
 * 5入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func5<R, P1, P2, P3, P4, P5> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @return
     */
    R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

}
