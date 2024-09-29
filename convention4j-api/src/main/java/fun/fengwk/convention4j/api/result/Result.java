package fun.fengwk.convention4j.api.result;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

/**
 * 服务调用的返回结果，一旦使用了Result作为返回值，
 * 意味着方法返回结果将存储在Result中，
 * 如果出现异常也不会抛出，所有的错误信息应该存储在Result中。
 *
 * @author fengwk
 */
public interface Result<T> extends Serializable {

    /**
     * 判断调用是否成功，true表示调用成功，false表示调用失败。
     *
     * @return 调用结果是否成功。
     */
    default boolean isSuccess() {
        return HttpStatus.is2xx(getStatus());
    }

    /**
     * 与http状态保持一致。
     *
     * @return http状态码。
     */
    int getStatus();

    /**
     * 获取编码。
     *
     * @return 编码。
     */
    String getCode();

    /**
     * 简洁明了的结果信息描述。
     *
     * @return 结果信息描述。
     */
    String getMessage();

    /**
     * 获取调用返回值。
     *
     * @return 调用返回值。
     */
    T getData();

    /**
     * 获取错误详情列表，如果调用失败可能会返回一个存放了各项失败具体原因的列表，
     * 并且必须注意不要将错误信息列表作为debug手段，禁止通过该信息向调用方传递敏感信息。
     *
     * @return 错误信息表。
     */
    Map<String, Object> getErrors();

    /**
     * 获取错误编码。
     *
     * @return 错误码，如果调用结果是成功则返回null。
     */
    ConventionErrorCode getErrorCode();

    /**
     * 将当前结果映射为其它类型。
     *
     * @param mapper 映射函数。
     * @param <R>    映射到的类型。
     * @return 映射后的结果。
     */
    <R> Result<R> map(Function<T, R> mapper);

    /**
     * 如果成功则获取数据，否则返回默认值。
     *
     * @param defaultValue 默认值。
     * @return 数据值。
     */
    default T orElseGet(T defaultValue) {
        return isSuccess() ? getData() : defaultValue;
    }

    /**
     * 如果成功则获取数据，否则将抛出异常。
     *
     * @return 数据值。
     */
    default T orElseThrow() {
        if (isSuccess()) {
            return getData();
        }
        throw getErrorCode().asThrowable();
    }

}
