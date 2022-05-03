package fun.fengwk.convention4j.common.function;

/**
 * 双入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func2T1<R, P1, P2, T1 extends Throwable> {

    /**
     * 执行函数。
     *
     * @param p1
     * @param p2
     * @return
     */
    R apply(P1 p1, P2 p2) throws T1;

}
