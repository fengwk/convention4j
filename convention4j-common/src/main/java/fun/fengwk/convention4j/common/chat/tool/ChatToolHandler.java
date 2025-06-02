package fun.fengwk.convention4j.common.chat.tool;

/**
 * @author fengwk
 */
public interface ChatToolHandler {

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    String getName();

    /**
     * 获取函数描述
     *
     * @return 函数名称
     */
    String getDescription();

    /**
     * 获取函数的参数
     *
     * @return 函数的参数
     */
    JsonSchema getParameters();

    /**
     * 调用函数
     *
     * @param arguments 参数表
     * @return 返回结果
     */
    String call(String arguments);

}
