package fun.fengwk.convention4j.common.code;

/**
 * 通用状态码码表。
 * 
 * @author fengwk
 */
public enum CommonCodeTable implements CodeTable {

    /**
     * 成功。
     */
    SUCCESS("0"),

    /**
     * 参数异常。
     */
    ILLEGAL_ARGUMENT(encodeCode("0001")),

    /**
     * 状态异常。
     */
    ILLEGAL_STATE(encodeCode("0002")),

    /**
     * 找不到资源。
     */
    RESOURCE_NOT_FOUND(encodeCode("0003")),

    /**
     * 未授权。
     */
    UNAUTHORIZED(encodeCode("0004")),

    /**
     * 无权访问。
     */
    FORBIDDEN(encodeCode("0005")),

    /**
     * 数据源错误。
     */
    DATASOURCE_ERROR(encodeCode("0006")),

    /**
     * 未支持的操作。
     */
    UNSUPPORTED_OPERATION(encodeCode("0007")),

    /**
     * 等待超时。
     */
    WAIT_TIMEOUT(encodeCode("0008")),

    /**
     * 中断异常。
     */
    INTERRUPTED(encodeCode("0009")),

    /**
     * 未知的错误。
     */
    UNKNOWN(encodeCode("9999")),

    ;

    private static final String COMMON = "C";

    private final String code;
    
    CommonCodeTable(String code) {
        this.code = code;
    }

    static String encodeCode(String num) {
        return ErrorCode.encodeCode(COMMON, num);
    }

    @Override
    public String getCode() {
        return code;
    }

}
