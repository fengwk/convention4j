package fun.fengwk.convention.api.code;

import java.io.Serializable;

/**
 * 状态码。
 * 
 * <p>
 * 状态码表示程序的运行状态，使用{@link SuccessCode}表示成功，{@link ErrorCode}表示失败。
 * </p>
 * 
 * @author fengwk
 */
public interface Code extends Serializable {
    
    /**
     * 获取码值。
     * 
     * @return
     */
    String getCode();
    
}
