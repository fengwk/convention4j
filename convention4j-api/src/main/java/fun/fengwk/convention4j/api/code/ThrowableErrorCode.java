package fun.fengwk.convention4j.api.code;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

/**
 * 错误码异常。
 *
 * @author fengwk
 */
public abstract class ThrowableErrorCode extends RuntimeException implements ResolvedErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ResolvedErrorCode resolvedErrorCode;

    /**
     *
     * @param resolvedErrorCode not null
     */
    public ThrowableErrorCode(ResolvedErrorCode resolvedErrorCode) {
        super(resolvedErrorCode.getMessage());
        this.resolvedErrorCode = Objects.requireNonNull(resolvedErrorCode, "resolvedErrorCode must not be null");
    }

    /**
     *
     * @param resolvedErrorCode not null
     * @param cause 造成原因，允许用null来表示不存在或未知
     */
    public ThrowableErrorCode(ResolvedErrorCode resolvedErrorCode, Throwable cause) {
        super(resolvedErrorCode.getMessage(), cause);
        this.resolvedErrorCode = Objects.requireNonNull(resolvedErrorCode, "resolvedErrorCode must not be null");
    }

    @Override
    public String getCode() {
        return resolvedErrorCode.getCode();
    }

    @Override
    public Map<String, Object> getErrorContext() {
        return resolvedErrorCode.getErrorContext();
    }

    /**
     * 获取当前包含的已解析的错误码
     *
     * @return 已解析的错误码
     */
    public ResolvedErrorCode getErrorCode() {
        return resolvedErrorCode;
    }

}
