package fun.fengwk.convention4j.common.function;

/**
 * 无入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func0T1<R, T1 extends Throwable> {

    /**
     * 执行函数。
     *
     * @return
     */
    R apply() throws T1;

}
