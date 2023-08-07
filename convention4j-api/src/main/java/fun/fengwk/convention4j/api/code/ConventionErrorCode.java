package fun.fengwk.convention4j.api.code;

import fun.fengwk.convention4j.api.code.DomainErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodePrototypeFactory;
import fun.fengwk.convention4j.api.code.HttpStatus;

/**
 * @author fengwk
 */
public interface ConventionErrorCode extends DomainErrorCode, ErrorCodePrototypeFactory {

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
