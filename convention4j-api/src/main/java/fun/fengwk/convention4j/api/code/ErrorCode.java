package fun.fengwk.convention4j.api.code;

/**
 * 错误编码信息。
 *
 * @author fengwk
 */
public interface ErrorCode extends Status {

    /**
     * 获取错误编码。
     *
     * @return 错误编码。
     */
    String getCode();

    /**
     * 转换当前错误编码为可抛出的异常形式。
     *
     * @return 错误码异常。
     */
    default ThrowableErrorCode asThrowable() {
        ErrorCode errorCode = this;
        if (this instanceof ErrorCodePrototypeFactory) {
            errorCode = ((ErrorCodePrototypeFactory) this).create();
        }
        return new ThrowableErrorCode(errorCode);
    }

    /**
     * 转换当前错误编码为可抛出的异常形式。
     *
     * @return 错误码异常。
     */
    default ThrowableErrorCode asThrowable(Throwable cause) {
        ErrorCode errorCode = this;
        if (this instanceof ErrorCodePrototypeFactory) {
            errorCode = ((ErrorCodePrototypeFactory) this).create();
        }
        return new ThrowableErrorCode(errorCode, cause);
    }

}
