package fun.fengwk.convention.api.code;

import static fun.fengwk.convention.api.code.ErrorCode.SOURCE_A;
import static fun.fengwk.convention.api.code.ErrorCode.SOURCE_B;
import static fun.fengwk.convention.api.code.ErrorCode.encodeCode;

/**
 * 通用错误码表。
 * 
 * @author fengwk
 */
public class ErrorCodes {

    /* A */
    public static final String ILLEGAL_ARGUMENT   = encodeCode(SOURCE_A, "0001");
    public static final String RESOURCE_NOT_FOUND = encodeCode(SOURCE_A, "0002");
    public static final String UNAUTHORIZED       = encodeCode(SOURCE_A, "0003");
    public static final String FORBIDDEN          = encodeCode(SOURCE_A, "0004");
    
    /* B */
    public static final String ILLEGAL_STATE      = encodeCode(SOURCE_B, "0001");
    
    /* C */
    
    
    private ErrorCodes() {}
    
}
