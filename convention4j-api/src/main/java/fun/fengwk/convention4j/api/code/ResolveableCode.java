package fun.fengwk.convention4j.api.code;

/**
 * 可解析的编码
 *
 * @author fengwk
 * @see CodeMessageResolver
 */
public interface ResolveableCode extends Code {

    /**
     * 将当前编码解析为ResolvedCode
     *
     * @return 解析后的编码
     */
    ResolvedCode resolve();

    /**
     * 将当前编码解析为ResolvedCode
     *
     * @param context 上下文信息
     * @return 解析后的编码
     */
    ResolvedCode resolve(Object context);

    /**
     * 将当前编码解析为ResolvedCode
     *
     * @param resolvedMessage 解析后的message
     * @return 解析后的编码
     */
    ResolvedCode resolve(String resolvedMessage);

}
