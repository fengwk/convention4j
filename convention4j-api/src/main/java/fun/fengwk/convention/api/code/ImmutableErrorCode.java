package fun.fengwk.convention.api.code;

import com.google.common.collect.ImmutableMap;

import java.util.Objects;

/**
 * 
 * @author fengwk
 */
public class ImmutableErrorCode implements ErrorCode {

    private static final long serialVersionUID = 1L;
    
    private final String code;
    private final String message;
    private final ImmutableMap<String, ?> errors;
    
    public ImmutableErrorCode(String code, String message, ImmutableMap<String, ?> errors) {
        this.code = Objects.requireNonNull(code);
        this.message = message;
        this.errors = Objects.requireNonNull(errors);
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
    public ImmutableMap<String, ?> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>", code, message);
    }

}
