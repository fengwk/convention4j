package fun.fengwk.convention4j.common.result;

import fun.fengwk.convention4j.common.code.Code;
import fun.fengwk.convention4j.common.code.ErrorCode;

import java.util.Map;
import java.util.Objects;

/**
 * 用于构建{@link Result}的工厂方法集。
 * 
 * @author fengwk
 */
public class Results {
    
    private Results() {}

    /**
     * 创建成功的返回结果。
     * 
     * @param <T>
     * @param code not empty
     * @param message
     * @return
     */
    public static <T> Result<T> success(String code, String message) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code cannot be empty");
        }

        return new ResultImpl<>(true, code, message, null, null);
    }

    /**
     * 创建成功的返回结果。
     * 
     * @param <T>
     * @param code not empty
     * @param message
     * @param data
     * @return
     */
    public static <T> Result<T> success(String code, String message, T data) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code cannot be empty");
        }

        return new ResultImpl<>(true, code, message, data, null);
    }
    
    /**
     * 使用{@link Code#SUCCESS}创建成功的返回结果。
     * 
     * @param <T>
     * @return
     */
    public static <T> Result<T> success() {
        return new ResultImpl<>(true, Code.SUCCESS.getCode(), null, null, null);
    }
    
    /**
     * 使用{@link Code#SUCCESS}创建成功的返回结果。
     * 
     * @param <T>
     * @param data
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new ResultImpl<>(true, Code.SUCCESS.getCode(), null, data, null);
    }
    
    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param code not empty
     * @param message
     * @return
     */
    public static <T> Result<T> error(String code, String message) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code cannot be empty");
        }

        return new ResultImpl<>(false, code, message, null, null);
    }
    
    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param code not empty
     * @param message
     * @param errors not null
     * @return
     */
    public static <T> Result<T> error(String code, String message, Map<String, Object> errors) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code cannot be empty");
        }
        Objects.requireNonNull(errors, "errors cannot be null");

        return new ResultImpl<>(false, code, message, null, errors);
    }
    
    /**
     * 使用{@link ErrorCode}创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode not null
     * @return
     */
    public static <T> Result<T> of(ErrorCode errorCode) {
        Objects.requireNonNull(errorCode, "errorCode cannot be null");

        return new ResultImpl<>(false, errorCode.getCode(), errorCode.getMessage(), null, null);
    }
    
    /**
     * 使用{@link ErrorCode}创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode not null
     * @param errors not null
     * @return
     */
    public static <T> Result<T> of(ErrorCode errorCode, Map<String, ?> errors) {
        Objects.requireNonNull(errorCode, "errorCode cannot be null");
        Objects.requireNonNull(errors, "errors cannot be null");

        return new ResultImpl<>(false, errorCode.getCode(), errorCode.getMessage(), null, errors);
    }

}
