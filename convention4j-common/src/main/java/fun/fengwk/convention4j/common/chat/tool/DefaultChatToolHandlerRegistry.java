package fun.fengwk.convention4j.common.chat.tool;

import fun.fengwk.convention4j.common.chat.request.ChatTool;
import fun.fengwk.convention4j.common.chat.request.ChatToolFunction;
import fun.fengwk.convention4j.common.chat.util.ChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class DefaultChatToolHandlerRegistry implements ChatToolHandlerRegistry {

    private final ConcurrentMap<String, ChatToolHandler> registry = new ConcurrentHashMap<>();

    @Override
    public List<ChatTool> getTools() {
        return registry.values().stream()
            .map(this::convert).collect(Collectors.toList());
    }

    @Override
    public void registerBeanIfNecessary(Object bean) {
        ChatToolHandlerParser toolFunctionParser = new ChatToolHandlerParser();
        List<ChatToolHandler> handlers = toolFunctionParser.parse(bean);
        handlers.forEach(this::registerHandler);
    }

    @Override
    public void registerHandler(ChatToolHandler handler) {
        if (!registerHandlerIfAbsent(handler)) {
            throw new IllegalStateException("duplicate tool function handler: " + handler.getName());
        }
    }

    @Override
    public boolean registerHandlerIfAbsent(ChatToolHandler handler) {
        return registry.putIfAbsent(handler.getName(), handler) == null;
    }

    @Override
    public boolean unregisterHandler(String name) {
        return registry.remove(name) != null;
    }

    @Override
    public ChatToolHandler getHandler(String name) {
        return registry.get(name);
    }

    @Override
    public ChatToolHandler getHandlerRequired(String name) {
        ChatToolHandler handler = getHandler(name);
        if (handler == null) {
            throw new IllegalStateException(String.format("handler '%s' not exists", name));
        }
        return handler;
    }

    @Override
    public Set<String> getAllNames() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    private ChatTool convert(ChatToolHandler handler) {
        ChatTool chatTool = new ChatTool();
        ChatUtils.setFunctionTool(chatTool);

        ChatToolFunction function = new ChatToolFunction();
        function.setDescription(handler.getDescription());
        function.setName(handler.getName());
        function.setParameters(handler.getParameters());
        chatTool.setFunction(function);

        return chatTool;
    }

}