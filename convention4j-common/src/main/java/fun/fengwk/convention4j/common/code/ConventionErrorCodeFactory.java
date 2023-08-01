package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.HttpStatus;

/**
 * @author fengwk
 */
public interface ConventionErrorCodeFactory extends DomainErrorCode, ErrorCodePrototypeFactory {

    /**
     * 获取http状态码。
     *
     * @return
     */
    HttpStatus getHttpStatus();

    @Override
    default int getStatus() {
        return getHttpStatus().getStatus();
    }

    @Override
    default String getMessage() {
        return getHttpStatus().getMessage();
    }

}
