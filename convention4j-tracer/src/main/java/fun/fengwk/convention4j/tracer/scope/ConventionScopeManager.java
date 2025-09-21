package fun.fengwk.convention4j.tracer.scope;

import io.opentracing.ScopeManager;

/**
 * @author fengwk
 */
public interface ConventionScopeManager extends ScopeManager, AutoCloseable {
}
