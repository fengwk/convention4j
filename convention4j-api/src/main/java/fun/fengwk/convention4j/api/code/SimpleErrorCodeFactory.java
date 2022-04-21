package fun.fengwk.convention4j.api.code;

import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class SimpleErrorCodeFactory extends ErrorCodeFactory {

    @Override
    protected ErrorCode doCreate(String errorCode, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, null, errors);
    }

    @Override
    protected ErrorCode doCreate(String errorCode, String message, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, message, errors);
    }

}
