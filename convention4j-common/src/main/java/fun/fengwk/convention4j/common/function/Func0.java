package fun.fengwk.convention4j.common.function;

/**
 * 无入参函数。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface Func0<R> {

    /**
     * 执行函数。
     *
     * @return
     */
    R apply();

}
