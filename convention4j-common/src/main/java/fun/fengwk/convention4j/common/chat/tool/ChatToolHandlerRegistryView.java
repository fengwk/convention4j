package fun.fengwk.convention4j.common.chat.tool;

import fun.fengwk.convention4j.common.chat.request.ChatTool;

import java.util.List;
import java.util.Set;

/**
 * @author fengwk
 */
public interface ChatToolHandlerRegistryView {

    /**
     * 获取当前注册表中的所有工具
     *
     * @return tools
     */
    List<ChatTool> getTools();

    /**
     * 获取指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return ToolFunctionHandler
     */
    ChatToolHandler getHandler(String name);

    /**
     * 获取指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return ToolFunctionHandler
     * @throws IllegalStateException 如果不存在指定名称的handler将抛出该异常
     */
    ChatToolHandler getHandlerRequired(String name);

    /**
     * 获取所有注册的处理器名称
     *
     * @return 所有注册的处理器名称
     */
    Set<String> getAllNames();

}
