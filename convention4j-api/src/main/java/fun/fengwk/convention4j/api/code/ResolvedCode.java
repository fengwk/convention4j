package fun.fengwk.convention4j.api.code;

/**
 * 标记接口说明当前Code已被解析过。
 *
 * @author fengwk
 */
public interface ResolvedCode extends Code {

    /**
     * 获取已被解析过的message
     *
     * @return 已被解析过的message
     */
    @Override
    String getMessage();

}
