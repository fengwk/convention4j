package fun.fengwk.convention4j.tracer.scope;

import fun.fengwk.convention4j.common.util.OrderedObject;
import io.opentracing.ScopeManager;

/**
 * @author fengwk
 */
public interface ConventionScopeManager extends ScopeManager, OrderedObject, AutoCloseable {
}
