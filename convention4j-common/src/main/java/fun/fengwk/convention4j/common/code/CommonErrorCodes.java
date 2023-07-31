package fun.fengwk.convention4j.common.code;

/**
 * 通用状态码码表。
 * 
 * @author fengwk
 */
public enum CommonErrorCodes implements ErrorCodes {

    /**
     * 参数异常。
     */
    ILLEGAL_ARGUMENT("0001"),

    /**
     * 状态异常。
     */
    ILLEGAL_STATE("0002"),

    /**
     * 找不到资源。
     */
    RESOURCE_NOT_FOUND("0003"),

    /**
     * 未授权。
     */
    UNAUTHORIZED("0004"),

    /**
     * 无权访问。
     */
    FORBIDDEN("0005"),

    /**
     * 数据源错误。
     */
    DATASOURCE_ERROR("0006"),

    /**
     * 未支持的操作。
     */
    UNSUPPORTED_OPERATION("0007"),

    /**
     * 等待超时。
     */
    WAIT_TIMEOUT("0008"),

    /**
     * 中断异常。
     */
    INTERRUPTED("0009"),

    /**
     * 中断异常。
     */
    SYSTEM_ERROR("0010"),

    /**
     * 中断异常。
     */
    SYSTEM_BUSY("0011"),

    /**
     * 未知的错误。
     */
    UNKNOWN("9999"),

    ;

    private static final String COMMON = "C";

    private final String value;
    
    CommonErrorCodes(String value) {
        this.value = value;
    }

    @Override
    public String getDomain() {
        return COMMON;
    }

    @Override
    public String getValue() {
        return value;
    }

}
