package fun.fengwk.convention4j.api.code;


import java.util.Map;

/**
 * 错误码
 *
 * @author fengwk
 */
public interface ErrorCode extends Code {

    /**
     * 获取不可变的错误信息上下文
     *
     * @return 返回不可变的错误信息上下文信息
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

    /**
     * 添加错误上下文
     *
     * @param errorContext 错误上下文
     * @return 添加错误上下文后的ErrorCode
     */
    ErrorCode withErrorContext(Map<String, Object> errorContext);

}
