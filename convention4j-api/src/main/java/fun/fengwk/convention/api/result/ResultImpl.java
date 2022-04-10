package fun.fengwk.convention.api.result;

import com.google.common.collect.ImmutableMap;

import java.util.Objects;

/**
 * 
 * @author fengwk
 */
public class ResultImpl<T> implements Result<T> {
    
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final ImmutableMap<String, ?> errors;
    
    public ResultImpl(boolean success, String code, String message, T data, ImmutableMap<String, ?> errors) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    @Override
    public boolean isSuccess() {
        return success;
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
    public T getData() {
        return data;
    }

    @Override
    public ImmutableMap<String, ?> getErrors() {
        return errors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, code, message, data, errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ResultImpl<?> other = (ResultImpl<?>) obj;
        return Objects.equals(success, other.success) 
                && Objects.equals(code, other.code)
                && Objects.equals(message, other.message)
                && Objects.equals(data, other.data)
                && Objects.equals(errors, other.errors);
    }

    @Override
    public String toString() {
        return "ResultImpl [success=" + success + ", code=" + code + ", message=" + message + ", data=" + data
                + ", errors=" + errors + "]";
    }

}
