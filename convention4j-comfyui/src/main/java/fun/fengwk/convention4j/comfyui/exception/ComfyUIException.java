package fun.fengwk.convention4j.comfyui.exception;

/**
 * ComfyUI 基础异常
 *
 * @author fengwk
 */
public class ComfyUIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ComfyUIException(String message) {
        super(message);
    }

    public ComfyUIException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComfyUIException(Throwable cause) {
        super(cause);
    }
}