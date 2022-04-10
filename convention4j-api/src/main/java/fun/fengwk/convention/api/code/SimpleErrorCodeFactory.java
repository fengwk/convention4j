package fun.fengwk.convention.api.code;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author fengwk
 */
public class SimpleErrorCodeFactory extends ErrorCodeFactory {

    @Override
    protected ErrorCode doCreate(String errorCode, ImmutableMap<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, null, errors);
    }

    @Override
    protected ErrorCode doCreate(String errorCode, String message, ImmutableMap<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, message, errors);
    }

}
