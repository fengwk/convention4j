package fun.fengwk.convention4j.api.code;

import java.util.Collections;
import java.util.Map;

/**
 * 原型错误码。
 *
 * @author fengwk
 * @see ErrorCodeMessageResolver
 */
public interface PrototypeErrorCode extends ErrorCode {

    @Override
    default ReslovedThrowableErrorCode asThrowable() {
        return new ReslovedThrowableErrorCode(resolve());
    }

    @Override
    default ReslovedThrowableErrorCode asThrowable(Throwable cause) {
        return new ReslovedThrowableErrorCode(resolve(), cause);
    }

    /**
     * 解析当前错误码并创建一个新的错误码。
     *
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode resolve() {
        if (this instanceof ResolvedErrorCode) {
            return (ResolvedErrorCode) this;
        }
        Map<String, Object> errorContext = getErrorContext();
        return resolve(errorContext == null ? Collections.emptyMap() : errorContext);
    }

    /**
     * 解析当前错误码并创建一个新的错误码。
     *
     * @param errorContext 错误上下文。
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode resolve(Map<String, Object> errorContext) {
        String message = getMessage();
        if (!(this instanceof ResolvedErrorCode)) {
            ErrorCodeMessageResolver manager = ErrorCodeMessageManagerHolder.getInstance();
            if (manager != null) {
                String managerMessage = manager.resolveMessage(this);
                if (managerMessage != null) {
                    message = managerMessage;
                }
            }
        }
        return new ImmutableResolvedErrorCode(getStatus(), getCode(), message, errorContext);
    }

    /**
     * 解析当前错误码并创建一个新的错误码，允许指定上下文参数作为解析器的输入。
     *
     * @param ctx 上下文参数。
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode resolveWithContext(Object ctx) {
        Map<String, Object> errorContext = getErrorContext();
        return resolveWithContext(ctx, errorContext == null ? Collections.emptyMap() : errorContext);
    }

    /**
     * 解析当前错误码并创建一个新的错误码，允许指定上下文参数作为解析器的输入。
     *
     * @param ctx 上下文参数。
     * @param errorContext 错误上下文。
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode resolveWithContext(Object ctx, Map<String, Object> errorContext) {
        String message = getMessage();
        if (!(this instanceof ResolvedErrorCode)) {
            ErrorCodeMessageResolver manager = ErrorCodeMessageManagerHolder.getInstance();
            if (manager != null) {
                String managerMessage = manager.resolveMessage(this, ctx);
                if (managerMessage != null) {
                    message = managerMessage;
                }
            }
        }
        return new ImmutableResolvedErrorCode(getStatus(), getCode(), message, errorContext);
    }

    /**
     * 指定解析的错误消息进行创建。
     *
     * @param message 错误消息。
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode create(String message) {
        Map<String, Object> errorContext = getErrorContext();
        return create(message, errorContext == null ? Collections.emptyMap() : errorContext);
    }

    /**
     * 指定解析的错误消息进行创建。
     *
     * @param message 错误消息。
     * @param errorContext 错误上下文。
     * @return 解析后的错误码。
     */
    default ResolvedErrorCode create(String message, Map<String, Object> errorContext) {
        return new ImmutableResolvedErrorCode(getStatus(), getCode(), message, errorContext);
    }

}
