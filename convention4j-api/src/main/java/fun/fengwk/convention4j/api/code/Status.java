package fun.fengwk.convention4j.api.code;

/**
 * 状态编码，使用http状态码作为标准。
 *
 * @author fengwk
 * @see HttpStatus
 */
public interface Status {

    /**
     * 获取http状态码。
     *
     * @return http状态码。
     */
    int getStatus();

    /**
     * 获取状态码对应的信息。
     *
     * @return 状态码对应的信息。
     */
    String getMessage();

}
