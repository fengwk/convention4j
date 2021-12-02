package fun.fengwk.convention.api.code;

import java.util.Objects;

/**
 * 
 * @author fengwk
 */
public class ImmutableErrorCode implements ErrorCode {

    private static final long serialVersionUID = 1L;
    
    private final String code;
    private final String message;
    
    public ImmutableErrorCode(String code, String message) {
        this.code = Objects.requireNonNull(code);
        this.message = message;
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
    public String toString() {
        return String.format("<%s, %s>", code, message);
    }

}
