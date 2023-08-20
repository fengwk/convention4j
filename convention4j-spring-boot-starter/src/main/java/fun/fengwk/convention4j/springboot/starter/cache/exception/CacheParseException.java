package fun.fengwk.convention4j.springboot.starter.cache.exception;

/**
 * @author fengwk
 */
public class CacheParseException extends RuntimeException {

    public CacheParseException(String message) {
        super(message);
    }

    public CacheParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
