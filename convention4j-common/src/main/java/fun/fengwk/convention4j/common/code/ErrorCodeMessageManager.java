package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;

/**
 * 错误码信息管理器。
 *
 * @author fengwk
 */
public interface ErrorCodeMessageManager {

    /**
     * 获取指定错误码的错误信息。
     *
     * @param errorCode 错误码。
     * @return 错误码信息。
     */
    String getMessage(ErrorCode errorCode);

}
