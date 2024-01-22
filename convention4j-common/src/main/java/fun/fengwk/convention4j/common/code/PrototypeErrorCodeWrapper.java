package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.PrototypeErrorCode;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.util.Map;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class PrototypeErrorCodeWrapper implements PrototypeErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    @Override
    public String getCode() {
        return errorCode.getCode();
    }

    @Override
    public Map<String, Object> getErrorContext() {
        return errorCode.getErrorContext();
    }

    @Override
    public int getStatus() {
        return errorCode.getStatus();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

}
