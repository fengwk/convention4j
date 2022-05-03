package fun.fengwk.convention4j.common.runtimex;

import java.util.concurrent.ExecutionException;

/**
 * {@link ExecutionException}的Runtime版本。
 *
 * @see ExecutionException
 * @author fengwk
 */
public class RuntimeExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @see ExecutionException()
     */
    protected RuntimeExecutionException() { }

    /**
     * @see ExecutionException(String)
     * @param message
     */
    protected RuntimeExecutionException(String message) {
        super(message);
    }

    /**
     * @see ExecutionException(String, Throwable)
     * @param message
     * @param cause
     */
    public RuntimeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see ExecutionException(Throwable)
     * @param cause
     */
    public RuntimeExecutionException(Throwable cause) {
        super(cause);
    }

    /**
     * 从{@link ExecutionException}转化为{@link RuntimeExecutionException}。
     *
     * @param ex
     * @return
     */
    public static RuntimeExecutionException from(ExecutionException ex) {
        return ex.getCause() == null ? new RuntimeExecutionException(ex.getMessage())
                : new RuntimeExecutionException(ex.getMessage(), ex.getCause());
    }

    /**
     * 获取对应的{@link ExecutionException}视图。
     *
     * @return
     */
    public ExecutionException asExecutionException() {
        return new ExecutionException(getMessage(), getCause());
    }

}
