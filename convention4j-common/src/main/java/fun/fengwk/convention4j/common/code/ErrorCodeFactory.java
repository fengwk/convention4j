package fun.fengwk.convention4j.common.code;

import java.util.Collections;
import java.util.Map;

/**
 * 错误码生产工厂。
 * 
 * @author fengwk
 */
public abstract class ErrorCodeFactory {

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

    /**
     * 指定错误编码生产错误码。
     *
     * @param errorCode not empty
     * @return
     */
    public ErrorCode create(String errorCode) {
        return create(errorCode, Collections.emptyMap());
    }

    /**
     * 指定错误编码、指定错误内容生产错误码。
     *
     * @param errorCode not empty
     * @param errors not null
     * @return
     */
    public ErrorCode create(String errorCode, Map<String, ?> errors) {
        if (!ErrorCode.validateErrorCodeFormat(errorCode)) {
            throw new IllegalArgumentException("error code format error.");
        }

        return doCreate(errorCode, errors);
    }

    /**
     * 指定错误编码、指定错误信息生产错误码。
     *
     * @param errorCode not empty
     * @param message
     * @return
     */
    public ErrorCode create(String errorCode, String message) {
        return create(errorCode, message, Collections.emptyMap());
    }

    /**
     * 指定错误编码、指定错误信息、指定错误内容生产错误码。
     *
     * @param errorCode not empty
     * @param message
     * @param errors not null
     * @return
     */
    public ErrorCode create(String errorCode, String message, Map<String, ?> errors) {
        if (!ErrorCode.validateErrorCodeFormat(errorCode)) {
            throw new IllegalArgumentException("error code format error.");
        }

        return doCreate(errorCode, message, errors);
    }

    /**
     * 指定码表生产错误码。
     *
     * @param errorCodeTable not null
     * @return
     */
    public ErrorCode create(CodeTable errorCodeTable) {
        return create(errorCodeTable.getCode());
    }

    /**
     * 指定码表、指定错误内容生产错误码。
     *
     * @param errorCodeTable not null
     * @param errors not null
     * @return
     */
    public ErrorCode create(CodeTable errorCodeTable, Map<String, ?> errors) {
        return create(errorCodeTable.getCode(), errors);
    }

    /**
     * 指定码表、指定错误信息生产错误码。
     *
     * @param errorCodeTable not null
     * @param message
     * @return
     */
    public ErrorCode create(CodeTable errorCodeTable, String message) {
        return create(errorCodeTable.getCode(), message);
    }

    /**
     * 指定码表、指定错误信息、指定错误内容生产错误码。
     *
     * @param errorCodeTable not null
     * @param message
     * @param errors not null
     * @return
     */
    public ErrorCode create(CodeTable errorCodeTable, String message, Map<String, ?> errors) {
        return create(errorCodeTable.getCode(), message, errors);
    }

}
