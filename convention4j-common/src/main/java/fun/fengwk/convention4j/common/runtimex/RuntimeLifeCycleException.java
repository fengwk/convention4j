package fun.fengwk.convention4j.common.runtimex;

/**
 * 用于处理一些无需捕获的生命周期异常的场景。
 *
 * @see fun.fengwk.convention4j.common.lifecycle.LifeCycleException
 * @author fengwk
 */
public class RuntimeLifeCycleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimeLifeCycleException() {
        super();
    }

    public RuntimeLifeCycleException(String message) {
        super(message);
    }

    public RuntimeLifeCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeLifeCycleException(Throwable cause) {
        super(cause);
    }

}
