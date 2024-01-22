package fun.fengwk.convention4j.api.code;

import java.io.Serializable;

/**
 * 状态编码，使用http状态码作为标准。
 *
 * @author fengwk
 * @see HttpStatus
 */
public interface Status extends Serializable {

    /**
     * 获取状态码。
     *
     * @return 状态码。
     */
    int getStatus();

    /**
     * 获取状态码对应的信息。
     *
     * @return 状态码对应的信息。
     */
    String getMessage();

}
