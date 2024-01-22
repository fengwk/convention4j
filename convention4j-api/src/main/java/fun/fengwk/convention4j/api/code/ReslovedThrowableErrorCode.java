package fun.fengwk.convention4j.api.code;

import java.io.Serial;

/**
 * 已解析的错误码异常。
 *
 * @author fengwk
 */
public class ReslovedThrowableErrorCode extends ThrowableErrorCode implements ResolvedErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param errorCode not null
     */
    public ReslovedThrowableErrorCode(ResolvedErrorCode errorCode) {
        super(errorCode);
    }

    /**
     *
     * @param errorCode not null
     * @param cause 造成原因，允许用null来表示不存在或未知
     */
    public ReslovedThrowableErrorCode(ResolvedErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
