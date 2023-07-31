package fun.fengwk.convention4j.common.code;

import java.util.Collections;
import java.util.Map;

/**
 * 错误码生产工厂抽象类。
 * 
 * @author fengwk
 */
public abstract class AbstractErrorCodeFactory implements ErrorCodeFactory {

    /**
     * 指定错误编码、指定错误内容实际创建错误码。
     *
     * @param errorCode not empty
     * @param errors not null
     * @return
     */
    protected abstract ErrorCode doCreate(String errorCode, Map<String, ?> errors);

    /**
     * 指定错误编码、指定错误信息、指定错误内容实际创建错误码。
     *
     * @param errorCode not empty
     * @param message
     * @param errors not null
     * @return
     */
    protected abstract ErrorCode doCreate(String errorCode, String message, Map<String, ?> errors);

    @Override
    public ErrorCode create(String errorCode) {
        return create(errorCode, Collections.emptyMap());
    }

    @Override
    public ErrorCode create(String errorCode, Map<String, ?> errors) {
        if (!ErrorCode.validateErrorCodeFormat(errorCode)) {
            throw new IllegalArgumentException("error code format error.");
        }

        return doCreate(errorCode, errors);
    }

    @Override
    public ErrorCode create(String errorCode, String message) {
        return create(errorCode, message, Collections.emptyMap());
    }

    @Override
    public ErrorCode create(String errorCode, String message, Map<String, ?> errors) {
        if (!ErrorCode.validateErrorCodeFormat(errorCode)) {
            throw new IllegalArgumentException("error code format error.");
        }

        return doCreate(errorCode, message, errors);
    }

    @Override
    public ErrorCode create(ErrorCodes errorCodes) {
        return create(errorCodes.getCode());
    }

    @Override
    public ErrorCode create(ErrorCodes errorCodes, Map<String, ?> errors) {
        return create(errorCodes.getCode(), errors);
    }

    @Override
    public ErrorCode create(ErrorCodes errorCodes, String message) {
        return create(errorCodes.getCode(), message);
    }

    @Override
    public ErrorCode create(ErrorCodes errorCodes, String message, Map<String, ?> errors) {
        return create(errorCodes.getCode(), message, errors);
    }

}
