package fun.fengwk.convention4j.common.chat.tool;


import fun.fengwk.convention4j.common.chat.request.ChatTool;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class ChatToolHandlerRegistrySubView implements ChatToolHandlerRegistryView {

    private final ChatToolHandlerRegistry delegate;
    private final Set<String> names;

    public ChatToolHandlerRegistrySubView(ChatToolHandlerRegistry delegate, Collection<String> names) {
        this.delegate = Objects.requireNonNull(delegate);
        this.names = new HashSet<>(NullSafe.of(names));
    }

    @Override
    public List<ChatTool> getTools() {
        return delegate.getTools().stream()
            .filter(t -> names.contains(t .getFunction().getName()))
            .collect(Collectors.toList());
    }

    @Override
    public ChatToolHandler getHandler(String name) {
        if (!names.contains(name)) {
            return null;
        }
        return delegate.getHandler(name);
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
        HashSet<String> allNames = new HashSet<>(names);
        Set<String> delegateNames = delegate.getAllNames();
        allNames.retainAll(delegateNames);
        return allNames;
    }

}
