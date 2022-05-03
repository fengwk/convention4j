package fun.fengwk.convention4j.common.expression;

/**
 * @author fengwk
 */
public class ExpressionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionException(Throwable cause) {
        super(cause);
    }

}
