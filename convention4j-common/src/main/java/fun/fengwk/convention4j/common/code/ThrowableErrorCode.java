package fun.fengwk.convention4j.common.code;

import java.util.Map;

/**
 * 可抛出的错误码，允许使用者像处理异常一样处理错误码。
 *
 * <p>建议在抛出错误码异常前记录错误日志，因为此时上下文信息是最全面的，利于记录发生错误的原因。</p>
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
    public Map<String, ?> getErrors() {
        return errorCode.getErrors();
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        return String.format("%s: <%s, %s>", s, errorCode.getCode(), getMessage());
    }

}
