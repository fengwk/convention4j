package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，单入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc1T1<P1, T1 extends Throwable> {

    /**
     * 执行函数。
     *
     * @param p1
     */
    void apply(P1 p1) throws T1;

}
