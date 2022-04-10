package fun.fengwk.convention.api.result;

import com.google.common.collect.ImmutableMap;
import fun.fengwk.convention.api.code.ErrorCode;
import fun.fengwk.convention.api.code.SuccessCode;

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
     * @param code
     * @param message
     * @return
     */
    public static <T> Result<T> success(String code, String message) {
        return new ResultImpl<>(true, code, message, null, null);
    }

    /**
     * 创建成功的返回结果。
     * 
     * @param <T>
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T> Result<T> success(String code, String message, T data) {
        return new ResultImpl<>(true, code, message, data, null);
    }
    
    /**
     * 使用{@link SuccessCode2}创建成功的返回结果。
     * 
     * @param <T>
     * @return
     */
    public static <T> Result<T> success() {
        return new ResultImpl<>(true, SuccessCode.INSTANCE.getCode(), null, null, null);
    }
    
    /**
     * 使用{@link SuccessCode2}创建成功的返回结果。
     * 
     * @param <T>
     * @param data
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new ResultImpl<>(true, SuccessCode.INSTANCE.getCode(), null, data, null);
    }
    
    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param code
     * @param message
     * @return
     */
    public static <T> Result<T> error(String code, String message) {
        return new ResultImpl<>(false, code, message, null, null);
    }
    
    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param code
     * @param message
     * @param errors
     * @return
     */
    public static <T> Result<T> error(String code, String message, ImmutableMap<String, Object> errors) {
        return new ResultImpl<>(false, code, message, null, errors);
    }
    
    /**
     * 使用{@link ErrorCode}创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode
     * @return
     */
    public static <T> Result<T> of(ErrorCode errorCode) {
        return new ResultImpl<>(false, errorCode.getCode(), errorCode.getMessage(), null, null);
    }
    
    /**
     * 使用{@link ErrorCode}创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode
     * @param errors
     * @return
     */
    public static <T> Result<T> of(ErrorCode errorCode, ImmutableMap<String, ?> errors) {
        return new ResultImpl<>(false, errorCode.getCode(), errorCode.getMessage(), null, errors);
    }

}
