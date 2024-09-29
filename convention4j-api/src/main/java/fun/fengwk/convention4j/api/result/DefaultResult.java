package fun.fengwk.convention4j.api.result;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ImmutableResolvedConventionErrorCode;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Map;
import java.util.function.Function;

/**
 * 
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class DefaultResult<T> implements Result<T> {
    
    @Serial
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String code;
    private final String message;
    private final T data;
    private final Map<String, Object> errors;

    public DefaultResult(int status, String code, String message, T data, Map<String, Object> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
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
    public T getData() {
        return data;
    }

    @Override
    public Map<String, Object> getErrors() {
        return errors;
    }

    @Override
    public ConventionErrorCode getErrorCode() {
        if (isSuccess()) {
            return null;
        }
        // 返回一个ResolvedErrorCode，尊重错误码的解析避免被重复解析
        return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), getMessage(), errors);
    }

    @Override
    public <R> Result<R> map(Function<T, R> mapper) {
        R r = data == null ? null : mapper.apply(data);
        return new DefaultResult<>(status, code, message, r, errors);
    }

}