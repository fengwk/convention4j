package fun.fengwk.convention4j.common.result;

import fun.fengwk.convention4j.api.code.CommonSuccessCodes;
import fun.fengwk.convention4j.api.code.ConventionCode;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ResolvedCode;
import fun.fengwk.convention4j.api.result.DefaultResult;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用于构建{@link Result}的工厂方法集
 * 
 * @author fengwk
 */
public class Results {

    private Results() {}

    /**
     * 创建成功的返回结果
     * 
     * @param <T>
     * @param code 状态码
     * @return
     */
    public static <T> Result<T> success(ConventionCode code) {
        Objects.requireNonNull(code, "code must not be null");
        return new DefaultResult<>(code.getStatus(), code.getCode(), code.getMessage(), null, null);
    }

    /**
     * 创建成功的返回结果
     * 
     * @param <T>
     * @param code 状态码
     * @param data
     * @return
     */
    public static <T> Result<T> success(ConventionCode code, T data) {
        Objects.requireNonNull(code, "code must not be null");
        return new DefaultResult<>(code.getStatus(), code.getCode(), code.getMessage(), data, null);
    }

    /**
     * 创建失败的返回结果
     * 
     * @param <T>
     * @param errorCode 错误编码
     * @return
     */
    public static <T> Result<T> error(ConventionErrorCode errorCode) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        if (!(errorCode instanceof ResolvedCode)) {
            errorCode = errorCode.resolve();
        }
        Map<String, Object> errors = new LinkedHashMap<>(errorCode.getErrorContext());
        return new DefaultResult<>(errorCode.getStatus(), errorCode.getCode(),
            errorCode.getMessage(), null, errors);
    }
    
    /**
     * 创建失败的返回结果
     * 
     * @param <T>
     * @param errorCode 错误编码
     * @param errors
     * @return
     */
    public static <T> Result<T> error(ConventionErrorCode errorCode, Map<String, ?> errors) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        if (!(errorCode instanceof ResolvedCode)) {
            errorCode = errorCode.resolve();
        }
        Map<String, Object> finalErrors = new LinkedHashMap<>(errorCode.getErrorContext());
        finalErrors.putAll(NullSafe.of(errors));
        return new DefaultResult<>(errorCode.getStatus(), errorCode.getCode(),
            errorCode.getMessage(), null, finalErrors);
    }

    /**
     * success {@link CommonSuccessCodes#OK}的快捷方式
     * 推荐GET、PUT、PATCH、DELETE使用
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.1">200 OK</a>
     */
    public static <T> Result<T> ok() {
        return success(CommonSuccessCodes.OK);
    }

    /**
     * success {@link CommonSuccessCodes#OK}的快捷方式
     * 推荐GET、PUT、PATCH、DELETE使用
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.1">200 OK</a>
     */
    public static <T> Result<T> ok(T data) {
        return success(CommonSuccessCodes.OK, data);
    }

    /**
     * success {@link CommonSuccessCodes#CREATED}的快捷方式
     * 推荐POST使用
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.2">201 Created</a>
     */
    public static <T> Result<T> created() {
        return success(CommonSuccessCodes.CREATED);
    }

    /**
     * success {@link CommonSuccessCodes#CREATED}的快捷方式
     * 推荐POST使用
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.2">201 Created</a>
     */
    public static <T> Result<T> created(T data) {
        return success(CommonSuccessCodes.CREATED, data);
    }

    /**
     * success {@link CommonSuccessCodes#ACCEPTED}的快捷方式
     * 通常用于异步处理请求的接受，表示执行尚未完成，但已经被接受
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.3">202 Accepted</a>
     */
    public static <T> Result<T> accepted() {
        return success(CommonSuccessCodes.ACCEPTED);
    }

    /**
     * success {@link CommonSuccessCodes#ACCEPTED}的快捷方式
     * 通常用于异步处理请求的接受，表示执行尚未完成，但已经被接受
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.3">202 Accepted</a>
     */
    public static <T> Result<T> accepted(T data) {
        return success(CommonSuccessCodes.ACCEPTED, data);
    }

    /**
     * success {@link CommonSuccessCodes#NON_AUTHORITATIVE_INFORMATION}的快捷方式
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.4">Non-Authoritative Information</a>
     */
    public static <T> Result<T> nonAuthoritativeInformation() {
        return success(CommonSuccessCodes.NON_AUTHORITATIVE_INFORMATION);
    }

    /**
     * success {@link CommonSuccessCodes#NON_AUTHORITATIVE_INFORMATION}的快捷方式
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.4">Non-Authoritative Information</a>
     */
    public static <T> Result<T> nonAuthoritativeInformation(T data) {
        return success(CommonSuccessCodes.NON_AUTHORITATIVE_INFORMATION, data);
    }

    /**
     * success {@link CommonSuccessCodes#NO_CONTENT}的快捷方式
     * 不推荐在HTTP协议中返回该Result，在标准的HTTP协议中该Result不应当返回荷载因此会导致该Result内容丢失
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.5">204 No Content</a>
     * @see <a href="https://github.com/reactor/reactor-netty/issues/1057">Netty Server handles HTTP 204(no content) with response body #1057</a>
     */
    public static <T> Result<T> noContent() {
        return success(CommonSuccessCodes.NO_CONTENT);
    }

    /**
     * success {@link CommonSuccessCodes#RESET_CONTENT}的快捷方式
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.6">205 Reset Content</a>
     */
    public static <T> Result<T> resetContent(T data) {
        return success(CommonSuccessCodes.RESET_CONTENT, data);
    }

}
