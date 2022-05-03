package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，8入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc8T1<P1, P2, P3, P4, P5, P6, P7, P8, T1 extends Throwable> {

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
     */
    void apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) throws T1;

}
