package fun.fengwk.convention4j.common.sql.dynamic;

/**
 * 动态SQL异常。
 *
 * @author fengwk
 */
public class DynamicSqlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DynamicSqlException(String message) {
        super(message);
    }

    public DynamicSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicSqlException(Throwable cause) {
        super(cause);
    }

}
