package fun.fengwk.convention4j.api.code;

import java.io.Serializable;

/**
 * 编码
 *
 * @author fengwk
 */
public interface Code extends Serializable {

    /**
     * 获取编码
     *
     * @return 编码
     */
    String getCode();

    /**
     * 获取编码信息
     *
     * @return 编码对应的信息
     */
    String getMessage();

}
