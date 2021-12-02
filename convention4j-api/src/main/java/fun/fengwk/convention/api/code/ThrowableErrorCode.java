package fun.fengwk.convention.api.code;

/**
 * {@link ThrowableErrorCode}是一种异常类型的错误码，允许使用者像处理异常一样处理错误码。
 *
 * @author fengwk
 */
public class ThrowableErrorCode extends RuntimeException implements ErrorCode {

    private static final long serialVersionUID = 1L;
    
    private final String code;
    
    /**
     * 
     * @param errorCode not null
     */
    public ThrowableErrorCode(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    
    /**
     * 
     * @param errorCode not null
     * @param cause 造成原因，允许用null来表示不存在或未知
     */
    public ThrowableErrorCode(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        return String.format("%s: <%s, %s>", s, code, getMessage());
    }

}
