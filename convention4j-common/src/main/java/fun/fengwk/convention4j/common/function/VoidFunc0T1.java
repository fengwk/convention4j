package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，无入参抛出1个异常的函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc0T1<T1 extends Throwable> {

    /**
     * 执行函数。
     */
    void apply() throws T1;

}
