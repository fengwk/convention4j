package fun.fengwk.convention4j.api.code;

/**
 * 错误码异常。
 *
 * @author fengwk
 */
public class ThrowableErrorCode extends RuntimeException implements ErrorCode {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    /**
     *
     * @param errorCode not null
     */
    public ThrowableErrorCode(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     *
     * @param errorCode not null
     * @param cause 造成原因，允许用null来表示不存在或未知
     */
    public ThrowableErrorCode(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getCode() {
        return errorCode.getCode();
    }

    @Override
    public int getStatus() {
        return errorCode.getStatus();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
