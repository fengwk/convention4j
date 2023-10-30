package fun.fengwk.convention4j.common.cache.exception;

/**
 * @author fengwk
 */
public class CacheInitializationException extends CacheException {

    private static final long serialVersionUID = 1L;

    public CacheInitializationException() {
        super();
    }

    public CacheInitializationException(String message) {
        super(message);
    }

    public CacheInitializationException(String message, Object... args) {
        super(String.format(message, args));
    }

    public CacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheInitializationException(Throwable cause) {
        super(cause);
    }

}
