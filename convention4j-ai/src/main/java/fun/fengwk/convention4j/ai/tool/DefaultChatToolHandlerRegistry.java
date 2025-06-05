package fun.fengwk.convention4j.ai.tool;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
public class DefaultChatToolHandlerRegistry implements ToolFunctionHandlerRegistry {

    private final ConcurrentMap<String, ToolFunctionHandler> registry = new ConcurrentHashMap<>();

    @Override
    public void registerHandler(ToolFunctionHandler handler) {
        if (!registerHandlerIfAbsent(handler)) {
            throw new IllegalStateException("duplicate tool function handler: " + handler.getName());
        }
    }

    @Override
    public boolean registerHandlerIfAbsent(ToolFunctionHandler handler) {
        return registry.putIfAbsent(handler.getName(), handler) == null;
    }

    @Override
    public boolean unregisterHandler(String name) {
        return registry.remove(name) != null;
    }

    @Override
    public ToolFunctionHandler getHandler(String name) {
        return registry.get(name);
    }

    @Override
    public ToolFunctionHandler getHandlerRequired(String name) {
        ToolFunctionHandler handler = getHandler(name);
        if (handler == null) {
            throw new IllegalStateException(String.format("handler '%s' not exists", name));
        }
        return handler;
    }

    @Override
    public Set<String> getAllNames() {
        return Collections.unmodifiableSet(registry.keySet());
    }

}