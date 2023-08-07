package fun.fengwk.convention4j.api.code;

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
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        String message = getMessage();
        if (manager != null) {
            String managerMessage = manager.getMessage(this);
            if (managerMessage != null) {
                message = managerMessage;
            }
        }
        return new ImmutableErrorCode(getStatus(), getCode(), message);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，同时使用指定的上下文格式化错误信息。
     *
     * @param ctx
     * @return
     */
    default ErrorCode create(Object ctx) {
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        String message = getMessage();
        if (manager != null) {
            String managerMessage = manager.getMessage(this);
            if (managerMessage != null) {
                message = managerMessage;
            }
            message = manager.formatMessage(message, ctx);
        }
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
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        message = manager.formatMessage(message, ctx);
        return new ImmutableErrorCode(getStatus(), getCode(), message);
    }

}
