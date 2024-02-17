package fun.fengwk.convention4j.common.result;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ResolvedErrorCode;
import fun.fengwk.convention4j.api.code.Status;
import fun.fengwk.convention4j.api.code.SuccessCode;
import fun.fengwk.convention4j.api.result.DefaultResult;
import fun.fengwk.convention4j.api.result.Errors;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.code.PrototypeErrorCodeWrapper;

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
        return new DefaultResult<>(status.getStatus(), status.getMessage(), null, null);
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
        return new DefaultResult<>(status.getStatus(), status.getMessage(), data, null);
    }

    /**
     * 创建失败的返回结果。
     * 
     * @param <T>
     * @param errorCode 错误编码
     * @return
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        if (!(errorCode instanceof ResolvedErrorCode)) {
            errorCode = new PrototypeErrorCodeWrapper(errorCode).resolve();
        }
        Errors errors = new Errors();
        errors.setCode(errorCode.getCode());
        errors.putAll(errorCode.getErrorContext());
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
        if (!(errorCode instanceof ResolvedErrorCode)) {
            errorCode = new PrototypeErrorCodeWrapper(errorCode)
                .resolve((Map<String, Object>) errors);
        }
        Errors finalErrors = new Errors();
        finalErrors.setCode(errorCode.getCode());
        finalErrors.putAll(errorCode.getErrorContext());
        finalErrors.putAll(errors);
        return new DefaultResult<>(errorCode.getStatus(), errorCode.getMessage(), null, finalErrors);
    }

    /**
     * success {@link SuccessCode#OK}的快捷方式。
     * 推荐GET、PUT、PATCH、DELETE使用。
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.1">200 OK</a>
     */
    public static <T> Result<T> ok() {
        return success(SuccessCode.OK);
    }

    /**
     * success {@link SuccessCode#OK}的快捷方式。
     * 推荐GET、PUT、PATCH、DELETE使用。
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.1">200 OK</a>
     */
    public static <T> Result<T> ok(T data) {
        return success(SuccessCode.OK, data);
    }

    /**
     * success {@link SuccessCode#CREATED}的快捷方式。
     * 推荐POST使用。
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.2">201 Created</a>
     */
    public static <T> Result<T> created() {
        return success(SuccessCode.CREATED);
    }

    /**
     * success {@link SuccessCode#CREATED}的快捷方式。
     * 推荐POST使用。
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.2">201 Created</a>
     */
    public static <T> Result<T> created(T data) {
        return success(SuccessCode.CREATED, data);
    }

    /**
     * success {@link SuccessCode#ACCEPTED}的快捷方式。
     * 通常用于异步处理请求的接受，表示执行尚未完成，但已经被接受。
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.3">202 Accepted</a>
     */
    public static <T> Result<T> accepted() {
        return success(SuccessCode.ACCEPTED);
    }

    /**
     * success {@link SuccessCode#ACCEPTED}的快捷方式。
     * 通常用于异步处理请求的接受，表示执行尚未完成，但已经被接受。
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.3">202 Accepted</a>
     */
    public static <T> Result<T> accepted(T data) {
        return success(SuccessCode.ACCEPTED, data);
    }

    /**
     * success {@link SuccessCode#NON_AUTHORITATIVE_INFORMATION}的快捷方式。
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.4">Non-Authoritative Information</a>
     */
    public static <T> Result<T> nonAuthoritativeInformation() {
        return success(SuccessCode.NON_AUTHORITATIVE_INFORMATION);
    }

    /**
     * success {@link SuccessCode#NON_AUTHORITATIVE_INFORMATION}的快捷方式。
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.4">Non-Authoritative Information</a>
     */
    public static <T> Result<T> nonAuthoritativeInformation(T data) {
        return success(SuccessCode.NON_AUTHORITATIVE_INFORMATION, data);
    }

    /**
     * success {@link SuccessCode#NO_CONTENT}的快捷方式。
     * 不推荐在HTTP协议中返回该Result，在标准的HTTP协议中该Result不应当返回荷载因此会导致该Result内容丢失。
     *
     * @param <T>
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.5">204 No Content</a>
     * @see <a href="https://github.com/reactor/reactor-netty/issues/1057">Netty Server handles HTTP 204(no content) with response body #1057</a>
     */
    public static <T> Result<T> noContent() {
        return success(SuccessCode.NO_CONTENT);
    }

    /**
     * success {@link SuccessCode#RESET_CONTENT}的快捷方式。
     *
     * @return
     * @param <T>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.3.6">205 Reset Content</a>
     */
    public static <T> Result<T> resetContent(T data) {
        return success(SuccessCode.RESET_CONTENT, data);
    }

}
