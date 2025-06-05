package fun.fengwk.convention4j.ai.tool;


import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author fengwk
 */
public class ToolFunctionHandlerRegistrySubView implements ToolFunctionHandlersView {

    private final ToolFunctionHandlerRegistry delegate;
    private final Set<String> names;

    public ToolFunctionHandlerRegistrySubView(ToolFunctionHandlerRegistry delegate, Collection<String> names) {
        this.delegate = Objects.requireNonNull(delegate);
        this.names = new HashSet<>(NullSafe.of(names));
    }

    @Override
    public ToolFunctionHandler getHandler(String name) {
        if (!names.contains(name)) {
            return null;
        }
        return delegate.getHandler(name);
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
        HashSet<String> allNames = new HashSet<>(names);
        Set<String> delegateNames = delegate.getAllNames();
        allNames.retainAll(delegateNames);
        return allNames;
    }

}
