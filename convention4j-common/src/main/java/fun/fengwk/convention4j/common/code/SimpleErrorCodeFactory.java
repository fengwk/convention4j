package fun.fengwk.convention4j.common.code;

import java.util.Map;

/**
 * 简单错误码生产工厂。
 *
 * @author fengwk
 */
public class SimpleErrorCodeFactory extends AbstractErrorCodeFactory {

    @Override
    protected ErrorCode doCreate(String errorCode, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, null, errors);
    }

    @Override
    protected ErrorCode doCreate(String errorCode, String message, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, message, errors);
    }

}
