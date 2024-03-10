package fun.fengwk.convention4j.tracer;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ScopeImpl implements Scope {

    private final List<ScopeImpl> scopeStack;
    @Getter
    private final Span span;

    public ScopeImpl(List<ScopeImpl> scopeStack, Span span) {
        this.scopeStack = scopeStack;
        this.span = span;
    }

    @Override
    public void close() {
        scopeStack.remove(this);
    }

}
