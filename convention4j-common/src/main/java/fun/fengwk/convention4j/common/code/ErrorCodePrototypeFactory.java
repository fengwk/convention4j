package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ImmutableErrorCode;
import fun.fengwk.convention4j.common.expression.ExpressionUtils;

/**
 * 错误码原型工厂。
 *
 * @author fengwk
 */
public interface ErrorCodePrototypeFactory extends ErrorCode {

    /**
     * 使用当前错误码作为原型创建一个新的错误码。
     *
     * @return
     */
    default ErrorCode create() {
        return new ImmutableErrorCode(getStatus(), getCode(), getMessage(this));
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，同时使用指定的上下文格式化错误信息。
     *
     * @param ctx
     * @return
     */
    default ErrorCode create(Object ctx) {
        String message = getMessage(this);
        message = ExpressionUtils.format(message, ctx);
        return new ImmutableErrorCode(getStatus(), getCode(), message);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息。
     *
     * @param message
     * @return
     */
    default ErrorCode create(String message) {
        return new ImmutableErrorCode(getStatus(), getCode(), message);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息，同时使用指定的上下文格式化错误信息。
     *
     * @param message
     * @param ctx
     * @return
     */
    default ErrorCode create(String message, Object ctx) {
        message = ExpressionUtils.format(message, ctx);
        return new ImmutableErrorCode(getStatus(), getCode(), message);
    }

    /**
     * 获取指定错误码的错误信息。
     *
     * @param errorCode
     * @return
     */
    default String getMessage(ErrorCode errorCode) {
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        if (manager == null) {
            return errorCode.getMessage();
        }
        return manager.getMessage(errorCode);
    }

}
