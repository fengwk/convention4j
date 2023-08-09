package fun.fengwk.convention4j.api.code;

import java.util.Objects;

/**
 * @author fengwk
 */
public class ImmutableErrorCode implements ErrorCode {

    private final int status;
    private final String code;
    private final String message;

    public ImmutableErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
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
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableErrorCode that = (ImmutableErrorCode) o;
        return status == that.status && Objects.equals(code, that.code) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, code, message);
    }

    @Override
    public String toString() {
        return "ImmutableErrorCode{" +
            "status=" + status +
            ", code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }

}
