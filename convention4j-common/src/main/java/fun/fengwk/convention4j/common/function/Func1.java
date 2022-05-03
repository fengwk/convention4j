package fun.fengwk.convention4j.common.function;

/**
 * 单入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func1<R, P1> {

    /**
     * 执行函数。
     *
     * @param p1
     * @return
     */
    R apply(P1 p1);

}
