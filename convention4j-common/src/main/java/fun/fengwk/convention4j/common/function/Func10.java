package fun.fengwk.convention4j.common.function;

/**
 * 10入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func10<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {

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
     * @param p8
     * @param p9
     * @param p10
     * @return
     */
    R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

}
