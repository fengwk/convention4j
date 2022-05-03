package fun.fengwk.convention4j.common.result;

import java.io.Serializable;
import java.util.Map;

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
     * 一旦返回false，应该检查方法调用产生了哪些异常。
     *
     * @return
     */
    boolean isSuccess();

    /**
     * 获取调用返回的结果编码。
     *
     * @return
     */
    String getCode();
    
    /**
     * 获取调用结果信息，
     * 如果调用成功，该信息可以为null，
     * 如果调用失败，该信息应该简洁明了地让调用者快速了解失败原因。
     *
     * @return nullable
     */
    String getMessage();

    /**
     * 获取调用返回值。
     *
     * @return nullable
     */
    T getData();
    
    /**
     * 获取错误详情列表，如果调用成功该方法必然返回null，
     * 如果调用失败可能会返回一个存放了各项失败具体原因的列表，但这是可选的，
     * 并且必须注意不要将错误信息列表作为debug手段，禁止通过该信息向调用方传递敏感信息。
     * 
     * @return
     */
    Map<String, ?> getErrors();

}
