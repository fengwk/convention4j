package fun.fengwk.convention4j.springboot.starter.cache.exception;

/**
 * @author fengwk
 */
public class CacheExecuteException extends RuntimeException {

    public CacheExecuteException(String message) {
        super(message);
    }

    public CacheExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

}
