package fun.fengwk.convention4j.api.result;

import fun.fengwk.convention4j.api.code.HttpStatus;

import java.util.Objects;

/**
 * 
 * @author fengwk
 */
public class DefaultResult<T> implements Result<T> {
    
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String message;
    private final T data;
    private final Errors errors;

    public DefaultResult(int status, String message, T data, Errors errors) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    @Override
    public boolean isSuccess() {
        return HttpStatus.is2xx(status);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultResult<?> that = (DefaultResult<?>) o;
        return status == that.status && Objects.equals(message, that.message) && Objects.equals(data, that.data) && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, data, errors);
    }

    @Override
    public String toString() {
        return "DefaultResult{" +
            "status=" + status +
            ", message='" + message + '\'' +
            ", data=" + data +
            ", errors=" + errors +
            '}';
    }

}