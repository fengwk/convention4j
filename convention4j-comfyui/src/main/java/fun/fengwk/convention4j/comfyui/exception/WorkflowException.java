package fun.fengwk.convention4j.comfyui.exception;

/**
 * 工作流异常
 *
 * @author fengwk
 */
public class WorkflowException extends ComfyUIException {

    private static final long serialVersionUID = 1L;

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}