package fun.fengwk.convention4j.common.function;

/**
 * 单入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func1T1<R, P1, T1 extends Throwable> {

    /**
     * 执行函数。
     *
     * @param p1
     * @return
     */
    R apply(P1 p1) throws T1;

}
