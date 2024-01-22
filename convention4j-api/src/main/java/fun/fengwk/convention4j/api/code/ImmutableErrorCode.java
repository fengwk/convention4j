package fun.fengwk.convention4j.api.code;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
public class ImmutableErrorCode implements ErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String code;
    private final String message;
    private final Map<String, Object> errorContext;

    public ImmutableErrorCode(int status, String code, String message, Map<String, Object> errorContext) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errorContext = errorContext;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Map<String, Object> getErrorContext() {
        return errorContext;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableErrorCode that = (ImmutableErrorCode) o;
        return status == that.status && Objects.equals(code, that.code) && Objects.equals(message, that.message) && Objects.equals(errorContext, that.errorContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, code, message, errorContext);
    }

    @Override
    public String toString() {
        return "ImmutableErrorCode{" +
            "status=" + status +
            ", code='" + code + '\'' +
            ", message='" + message + '\'' +
            ", errorContext=" + errorContext +
            '}';
    }
}
