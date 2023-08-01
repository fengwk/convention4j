package fun.fengwk.convention4j.common.result;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.Status;
import fun.fengwk.convention4j.api.result.Errors;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.api.result.DefaultResult;
import fun.fengwk.convention4j.common.code.SuccessCodes;

import java.util.Map;

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
     * @param status 状态信息。
     * @return
     */
    public static <T> Result<T> success(Status status) {
        return new DefaultResult<>(status.getStatus(), status.getMessage(), null, new Errors());
    }

    /**
     * 创建成功的返回结果。
     * 
     * @param <T>
     * @param status 状态信息。
     * @param data
     * @return
     */
    public static <T> Result<T> success(Status status, T data) {
        return new DefaultResult<>(status.getStatus(), status.getMessage(), data, new Errors());
    }

    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode 错误编码
     * @return
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        Errors errors = new Errors();
        errors.setCode(errorCode.getCode());
        return new DefaultResult<>(errorCode.getStatus(), errorCode.getMessage(), null, errors);
    }
    
    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode 错误编码
     * @param errors not null
     * @return
     */
    public static <T> Result<T> error(ErrorCode errorCode, Map<String, ?> errors) {
        Errors finalErrors = new Errors();
        finalErrors.setCode(errorCode.getCode());
        finalErrors.putAll(errors);
        return new DefaultResult<>(errorCode.getStatus(), errorCode.getMessage(), null, finalErrors);
    }

    /**
     * success {@link SuccessCodes#OK)}的快捷方式。
     * 推荐GET、PUT、PATCH使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok() {
        return success(SuccessCodes.OK);
    }

    /**
     * success {@link SuccessCodes#OK)}的快捷方式。
     * 推荐GET、PUT、PATCH使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        return success(SuccessCodes.OK, data);
    }

    /**
     * success {@link SuccessCodes#CREATED)}的快捷方式。
     * 推荐POST使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> created() {
        return success(SuccessCodes.CREATED);
    }

    /**
     * success {@link SuccessCodes#CREATED)}的快捷方式。
     * 推荐POST使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> created(T data) {
        return success(SuccessCodes.CREATED, data);
    }

    /**
     * success {@link SuccessCodes#NO_CONTENT)}的快捷方式。
     * 推荐DELETE使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> noContent() {
        return success(SuccessCodes.NO_CONTENT);
    }

    /**
     * success {@link SuccessCodes#NO_CONTENT)}的快捷方式。
     * 推荐DELETE使用。
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> noContent(T data) {
        return success(SuccessCodes.NO_CONTENT, data);
    }

}
