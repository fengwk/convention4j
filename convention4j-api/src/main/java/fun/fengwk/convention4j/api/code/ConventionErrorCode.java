package fun.fengwk.convention4j.api.code;

import java.util.Collections;
import java.util.Map;

/**
 * @author fengwk
 */
public interface ConventionErrorCode extends DomainErrorCode, PrototypeErrorCode {

    /**
     * 获取http状态码。
     *
     * @return http状态码。
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

    @Override
    default Map<String, Object> getErrorContext() {
        return Collections.emptyMap();
    }

}
