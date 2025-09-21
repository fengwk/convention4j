package fun.fengwk.convention4j.tracer.scope.aspect;

import io.opentracing.SpanContext;

import java.util.Collections;
import java.util.Map;

/**
 * @author fengwk
 */
public class EmptyThreadScopeAspect implements ThreadScopeAspect {

    @Override
    public Map<String, String> onEnter(SpanContext spanContext) {
        return Collections.emptyMap();
    }

    @Override
    public void onExit(Map<String, String> store) {
        // do nothing
    }

}
