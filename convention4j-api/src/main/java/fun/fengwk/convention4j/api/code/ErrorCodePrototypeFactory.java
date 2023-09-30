package fun.fengwk.convention4j.api.code;

import java.util.Collections;
import java.util.Map;

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
        return createWithErrorContext(Collections.emptyMap());
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码。
     * 
     * @param errorContext
     * @return
     */
    default ErrorCode createWithErrorContext(Map<String, Object> errorContext) {
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        String message = getMessage();
        if (manager != null) {
            String managerMessage = manager.getMessage(this);
            if (managerMessage != null) {
                message = managerMessage;
            }
        }
        return new ImmutableErrorCode(getStatus(), getCode(), message, errorContext);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，同时使用指定的上下文格式化错误信息。
     *
     * @param ctx
     * @return
     */
    default ErrorCode create(Object ctx) {
        return createWithErrorContext(ctx, Collections.emptyMap());
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，同时使用指定的上下文格式化错误信息。
     *
     * @param ctx
     * @param errorContext
     * @return
     */
    default ErrorCode createWithErrorContext(Object ctx, Map<String, Object> errorContext) {
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        String message = getMessage();
        if (manager != null) {
            String managerMessage = manager.getMessage(this);
            if (managerMessage != null) {
                message = managerMessage;
            }
            message = manager.formatMessage(message, ctx);
        }
        return new ImmutableErrorCode(getStatus(), getCode(), message, errorContext);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息。
     *
     * @param message
     * @return
     */
    default ErrorCode create(String message) {
        return createWithErrorContext(message, Collections.emptyMap());
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息。
     *
     * @param message
     * @param errorContext
     * @return
     */
    default ErrorCode createWithErrorContext(String message, Map<String, Object> errorContext) {
        return new ImmutableErrorCode(getStatus(), getCode(), message, errorContext);
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息，同时使用指定的上下文格式化错误信息。
     *
     * @param message
     * @param ctx
     * @return
     */
    default ErrorCode create(String message, Object ctx) {
        return createWithErrorContext(message, ctx, Collections.emptyMap());
    }

    /**
     * 使用当前错误码作为原型创建一个新的错误码，并指定错误信息，同时使用指定的上下文格式化错误信息。
     *
     * @param message
     * @param ctx
     * @param errorContext
     * @return
     */
    default ErrorCode createWithErrorContext(String message, Object ctx, Map<String, Object> errorContext) {
        ErrorCodeMessageManager manager = ErrorCodeMessageManagerHolder.getInstance();
        message = manager.formatMessage(message, ctx);
        return new ImmutableErrorCode(getStatus(), getCode(), message, errorContext);
    }

}
