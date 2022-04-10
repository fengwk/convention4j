package fun.fengwk.convention.api.code;

import static fun.fengwk.convention.api.code.ErrorCode.*;

/**
 * 通用码表。
 * 
 * @author fengwk
 */
public enum CommonCodeTable implements CodeTable {

    SUCCESS("0"),

    /* A */

    /**
     * 参数错误。
     */
    A_ILLEGAL_ARGUMENT(encodeCode(SOURCE_A, "0001")),

    /**
     * 找不到资源。
     */
    A_RESOURCE_NOT_FOUND(encodeCode(SOURCE_A, "0002")),

    /**
     * 未授权。
     */
    A_UNAUTHORIZED(encodeCode(SOURCE_A, "0003")),

    /**
     * 无权访问。
     */
    A_FORBIDDEN(encodeCode(SOURCE_A, "0004")),

    /**
     * 来自于调用者的未知错误。
     */
    A_UNKNOWN(encodeCode(SOURCE_A, "9999")),

    /* B */

    /**
     * 程序状态异常。
     */
    B_ILLEGAL_STATE(encodeCode(SOURCE_B, "0001")),

    /**
     * 来自于当前系统的未知错误。
     */
    B_UNKNOWN(encodeCode(SOURCE_B, "9999")),

    /* C */

    /* [0100..0200)分配给数据源错误 */

    /**
     * 数据源错误。
     */
    C_DATASOURCE_ERROR(encodeCode(SOURCE_C, "0100")),

    /**
     * 来自于依赖系统的未知错误。
     */
    C_UNKNOWN(encodeCode(SOURCE_C, "9999")),

    ;

    private final String code;
    
    private CommonCodeTable(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

}
