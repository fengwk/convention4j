package fun.fengwk.convention4j.common.cache.exception;

/**
 * @author fengwk
 */
public class CacheExecuteException extends CacheException {

    private static final long serialVersionUID = 1L;

    public CacheExecuteException() {
        super();
    }

    public CacheExecuteException(String message) {
        super(message);
    }

    public CacheExecuteException(String message, Object... args) {
        super(String.format(message, args));
    }

    public CacheExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheExecuteException(Throwable cause) {
        super(cause);
    }

}
