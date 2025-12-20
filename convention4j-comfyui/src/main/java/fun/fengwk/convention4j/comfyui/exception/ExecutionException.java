package fun.fengwk.convention4j.comfyui.exception;

/**
 * 执行异常
 *
 * @author fengwk
 */
public class ExecutionException extends ComfyUIException {

    private static final long serialVersionUID = 1L;

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}