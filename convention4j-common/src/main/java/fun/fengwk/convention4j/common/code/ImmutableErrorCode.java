package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.common.StringUtils;

import java.util.Map;

/**
 * 不可变的错误码。
 *
 * @author fengwk
 */
public class ImmutableErrorCode implements ErrorCode {

    private static final long serialVersionUID = 1L;
    
    private final String code;
    private final String message;
    private final Map<String, ?> errors;

    /**
     *
     * @param code not empty
     * @param message
     * @param errors not null
     */
    public ImmutableErrorCode(String code, String message, Map<String, ?> errors) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("code cannot be empty");
        }
        if (errors == null) {
            throw new NullPointerException("errors cannot be null");
        }

        this.code = code;
        this.message = message;
        this.errors = errors;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, ?> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>", code, message);
    }

}
