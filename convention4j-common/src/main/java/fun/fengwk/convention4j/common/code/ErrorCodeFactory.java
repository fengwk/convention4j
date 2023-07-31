package fun.fengwk.convention4j.common.code;

import java.util.Map;

/**
 * 错误码生产工厂。
 * 
 * @author fengwk
 */
public interface ErrorCodeFactory {

    /**
     * 指定错误编码生产错误码。
     *
     * @param errorCode not empty
     * @return
     */
    ErrorCode create(String errorCode);

    /**
     * 指定错误编码、指定错误内容生产错误码。
     *
     * @param errorCode not empty
     * @param errors not null
     * @return
     */
    ErrorCode create(String errorCode, Map<String, ?> errors);

    /**
     * 指定错误编码、指定错误信息生产错误码。
     *
     * @param errorCode not empty
     * @param message
     * @return
     */
    ErrorCode create(String errorCode, String message);

    /**
     * 指定错误编码、指定错误信息、指定错误内容生产错误码。
     *
     * @param errorCode not empty
     * @param message
     * @param errors not null
     * @return
     */
    ErrorCode create(String errorCode, String message, Map<String, ?> errors);

    /**
     * 指定码表生产错误码。
     *
     * @param errorCodes not null
     * @return
     */
    ErrorCode create(ErrorCodes errorCodes);

    /**
     * 指定码表、指定错误内容生产错误码。
     *
     * @param errorCodes not null
     * @param errors not null
     * @return
     */
    ErrorCode create(ErrorCodes errorCodes, Map<String, ?> errors);

    /**
     * 指定码表、指定错误信息生产错误码。
     *
     * @param errorCodes not null
     * @param message
     * @return
     */
    ErrorCode create(ErrorCodes errorCodes, String message);

    /**
     * 指定码表、指定错误信息、指定错误内容生产错误码。
     *
     * @param errorCodes not null
     * @param message
     * @param errors not null
     * @return
     */
    ErrorCode create(ErrorCodes errorCodes, String message, Map<String, ?> errors);

}
