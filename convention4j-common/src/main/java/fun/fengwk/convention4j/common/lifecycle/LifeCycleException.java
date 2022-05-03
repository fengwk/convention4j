package fun.fengwk.convention4j.common.lifecycle;

/**
 * 生命周期异常。
 *
 * @author fengwk
 */
public class LifeCycleException extends Exception {

    private static final long serialVersionUID = 1L;

    public LifeCycleException() {
        super();
    }

    public LifeCycleException(String message) {
        super(message);
    }

    public LifeCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifeCycleException(Throwable cause) {
        super(cause);
    }

}
