package fun.fengwk.convention4j.api.code;


import java.util.Map;

/**
 * 错误码
 *
 * @author fengwk
 */
public interface ErrorCode extends Code {

    /**
     * 获取错误信息上下文
     *
     * @return 错误信息上下文。
     */
    Map<String, Object> getErrorContext();

    /**
     * 转换当前错误编码为可抛出的异常形式
     *
     * @return 错误码异常
     */
    ThrowableErrorCode asThrowable();

    /**
     * 转换当前错误编码为可抛出的异常形式
     *
     * @param context 上下文信息，用于message解析
     * @return 错误码异常
     */
    ThrowableErrorCode asThrowable(Object context);

    /**
     * 转换当前错误编码为可抛出的异常形式
     *
     * @param cause 错误原因
     * @return 错误码异常
     */
    ThrowableErrorCode asThrowable(Throwable cause);

    /**
     * 转换当前错误编码为可抛出的异常形式
     *
     * @param cause 错误原因
     * @param context 上下文信息，用于message解析
     * @return 错误码异常
     */
    ThrowableErrorCode asThrowable(Throwable cause, Object context);

}
