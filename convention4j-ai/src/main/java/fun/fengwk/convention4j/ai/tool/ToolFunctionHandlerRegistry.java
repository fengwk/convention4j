package fun.fengwk.convention4j.ai.tool;

/**
 * @author fengwk
 */
public interface ToolFunctionHandlerRegistry extends ToolFunctionHandlersView {

    /**
     * 注册ToolFunctionHandler
     *
     * @param handler ToolFunctionHandler
     * @throws IllegalStateException 如果重复注册了同名handler将抛出该异常
     */
    void registerHandler(ToolFunctionHandler handler);

    /**
     * 如果不存在同名ToolFunctionHandler则进行注册
     *
     * @param handler ToolFunctionHandler
     */
    boolean registerHandlerIfAbsent(ToolFunctionHandler handler);

    /**
     * 注销指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return 是否有ToolFunctionHandler被注销
     */
    boolean unregisterHandler(String name);

}
