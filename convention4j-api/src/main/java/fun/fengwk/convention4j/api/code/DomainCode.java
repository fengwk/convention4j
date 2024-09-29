package fun.fengwk.convention4j.api.code;

/**
 * 具备领域隔离能力的编码
 *
 * @author fengwk
 */
public interface DomainCode extends Code {

    String SEPARATOR = ".";

    @Override
    default String getCode() {
        return getDomain() + SEPARATOR + getDomainCode();
    }

    /**
     * 获取当前领域编码
     *
     * @return 当前领域编码
     */
    String getDomain();

    /**
     * 获取当前领域内的编码
     *
     * @return 当前领域内的编码
     */
    String getDomainCode();

}
