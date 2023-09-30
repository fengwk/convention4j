package fun.fengwk.convention4j.common.validation;

/**
 * 规约检查器。
 *
 * @author fengwk
 */
public interface ConventionChecker<T> {

    /**
     * 检查指定值是否符合约定，如果不符合约定请抛出运行时异常终端流程。
     *
     * @param value 指定值。
     * @throws RuntimeException 如果指定值不符合约定抛出错误码异常。
     */
    void check(T value);

}
