package fun.fengwk.convention4j.common.code;

import java.util.Map;
import java.util.Objects;

/**
 * 错误状态码集，用于描述状态码值。实现时可以参考{@link CommonErrorCodes}，这是一个典型的状态码码表实现。
 *
 * @author fengwk
 */
public interface ErrorCodes {

    /**
     * 获取错误码所属的领域。
     *
     * @return
     */
    String getDomain();

    /**
     * 获取错误码值，应当由四位数字编号组成。
     *
     * @return
     */
    String getValue();

    /**
     * 获取错误码。
     *
     * @return
     */
    default String getCode() {
        return ErrorCode.encodeCode(getDomain(), getValue());
    }

    /**
     * 获取当前错误码集的创建构建工厂。
     *
     * @return
     */
    default ErrorCodeFactory getErrorCodeFactory() {
        return GlobalErrorCodeFactory.getInstance();
    }

    /**
     * {@link ErrorCodeFactory#create(ErrorCodes)}
     */
    default ErrorCode create() {
        return getErrorCodeFactory().create(this);
    }

    /**
     * {@link ErrorCodeFactory#create(ErrorCodes, Map)}
     */
    default ErrorCode create(Map<String, ?> errors) {
        return getErrorCodeFactory().create(this, errors);
    }

    /**
     * {@link ErrorCodeFactory#create(ErrorCodes, String)}
     */
    default ErrorCode create(String message) {
        return getErrorCodeFactory().create(this, message);
    }

    /**
     * {@link ErrorCodeFactory#create(ErrorCodes, String, Map)}
     */
    default ErrorCode create(String message, Map<String, ?> errors) {
        return getErrorCodeFactory().create(this, message, errors);
    }

    /**
     * {@link ErrorCode#asThrowable()}
     */
    default ThrowableErrorCode asThrowable() {
        return create().asThrowable();
    }

    /**
     * {@link ErrorCode#asThrowable(Throwable)}
     */
    default ThrowableErrorCode asThrowable(Throwable cause) {
        return create().asThrowable();
    }

    /**
     * 检查是否与指定编码具有相同的编码值。
     *
     * @param code
     * @return
     */
    default boolean equalsCode(Code code) {
        return Objects.equals(getCode(), code.getCode());
    }

    /**
     * 检查是否与指定编码值相同。
     *
     * @param code
     * @return
     */
    default boolean equalsCode(String code) {
        return Objects.equals(getCode(), code);
    }

}
