package fun.fengwk.convention4j.common.sql.dynamic.node;

/**
 * 解释异常。
 *
 * @author fengwk
 */
public class InterpretException extends Exception {

    private static final long serialVersionUID = 1L;

    public InterpretException(String message) {
        super(message);
    }

    public InterpretException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterpretException(Throwable cause) {
        super(cause);
    }

}
