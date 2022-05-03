package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，3入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc3<P1, P2, P3> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @param p3
     */
    void apply(P1 p1, P2 p2, P3 p3);

}
