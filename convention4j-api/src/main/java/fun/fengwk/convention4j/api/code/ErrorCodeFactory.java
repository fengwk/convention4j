package fun.fengwk.convention4j.api.code;

import com.google.common.collect.ImmutableMap;

/**
 * 编码生产工厂。
 * 
 * @author fengwk
 */
public abstract class ErrorCodeFactory {

    /**
     * 指定错误编码、指定错误内容实际创建错误码。
     *
     * @param errorCode
     * @param errors
     * @return
     */
    protected abstract ErrorCode doCreate(String errorCode, ImmutableMap<String, ?> errors);

    /**
     * 指定错误编码、指定错误信息、指定错误内容实际创建错误码。
     *
     * @param errorCode not null
     * @param message
     * @param errors not null
     * @return
     */
    protected abstract ErrorCode doCreate(String errorCode, String message, ImmutableMap<String, ?> errors);

    /**
     * 指定错误编码生产错误码。
     *
     * @param errorCode not null
     * @return
     */
    public ErrorCode create(String errorCode) {
        return create(errorCode, ImmutableMap.of());
    }

    /**
     * 指定错误编码、指定错误内容生产错误码。
     *
     * @param errorCode not null
     * @param errors not null
     * @return
     */
    public ErrorCode create(String errorCode, ImmutableMap<String, ?> errors) {
        if (!ErrorCode.validateErrorCodeFormat(errorCode)) {
            throw new IllegalArgumentException("error code format error.");
        }

        return doCreate(errorCode, errors);
    }

    /**
     * 指定错误编码、指定错误信息生产错误码。
     *
     * @param errorCode not null
     * @param message
     * @return
     */
    public ErrorCode create(String errorCode, String message) {
        return create(errorCode, message, ImmutableMap.of());
    }

    /**
     * 指定错误编码、指定错误信息、指定错误内容生产错误码。
     *
     * @param errorCode not null
     * @param message
     * @param errors not null
     * @return
     */
    public ErrorCode create(String errorCode, String message, ImmutableMap<String, ?> errors) {
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
    public ErrorCode create(CodeTable errorCodeTable, ImmutableMap<String, ?> errors) {
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
    public ErrorCode create(CodeTable errorCodeTable, String message, ImmutableMap<String, ?> errors) {
        return create(errorCodeTable.getCode(), message, errors);
    }

}
