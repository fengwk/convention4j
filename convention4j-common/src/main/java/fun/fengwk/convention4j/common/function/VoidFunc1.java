package fun.fengwk.convention4j.common.function;

/**
 * 无返回值，单入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface VoidFunc1<P1> {

    /**
     * 执行函数。
     *
     * @param p1
     */
    void apply(P1 p1);

}
