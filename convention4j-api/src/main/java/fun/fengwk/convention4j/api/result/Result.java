package fun.fengwk.convention4j.api.result;

import fun.fengwk.convention4j.api.code.ErrorCode;

import java.io.Serializable;
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
    boolean isSuccess();

    /**
     * 与http状态保持一致。
     *
     * @return http状态码。
     */
    int getStatus();

    /**
     * 获取调用结果信息，如果调用成功，该信息可以为null，
     * 如果调用失败，该信息应该简洁明了地让调用者快速了解失败原因。
     *
     * @return nullable
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
    Errors getErrors();

    /**
     * 获取错误编码。
     *
     * @return 错误码，如果调用结果是成功则返回null。
     */
    ErrorCode getErrorCode();

    /**
     * 将当前结果映射为其它类型。
     *
     * @param mapper 映射函数。
     * @param <R> 映射到的类型。
     * @return 映射后的结果。
     */
    <R> Result<R> map(Function<T, R> mapper);

}
