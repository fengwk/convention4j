package fun.fengwk.convention4j.api.result;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

/**
 * REST或RPC调用时的返回结果。
 *
 * @author fengwk
 */
public interface Result<T> extends Serializable {

    /**
     * 判断调用是否成功，返回true说明调用成功，否则表示调用失败。
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
     * 获取调用结果信息，如果调用失败了，该信息应该简洁明了地让调用者快速了解失败原因。
     *
     * @return
     */
    String getMessage();

    /**
     * 获取调用返回值。
     *
     * @return
     */
    T getData();
    
    /**
     * 获取错误详情列表，如果调用成功该方法必然返回null，
     * 如果调用失败可能会返回一个存放了各项失败具体原因的列表，但这是可选的，并且必须注意不要将错误信息列表作为debug手段，禁止通过该信息向调用方传递敏感信息。
     * 
     * @return
     */
    ImmutableMap<String, ?> getErrors();

}
