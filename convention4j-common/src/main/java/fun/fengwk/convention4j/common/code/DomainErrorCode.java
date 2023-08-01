package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;

/**
 * 具备领域隔离能力的错误编码信息。
 *
 * @author fengwk
 */
public interface DomainErrorCode extends ErrorCode {

    String SEPARATOR = "_";

    @Override
    default String getCode() {
        return getDomain() + SEPARATOR + getDomainCode();
    }

    /**
     * 获取当前错误码所属领域。
     *
     * @return 错误码所属领域。
     */
    String getDomain();

    /**
     * 获取当前领域内的错误编码。
     *
     * @return 领域内的错误编码。
     */
    int getDomainCode();

}
