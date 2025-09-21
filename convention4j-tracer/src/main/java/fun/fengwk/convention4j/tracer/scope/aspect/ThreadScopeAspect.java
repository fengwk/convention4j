package fun.fengwk.convention4j.tracer.scope.aspect;

import io.opentracing.SpanContext;

import java.util.Map;

/**
 * 线程作用域切面
 *
 * @author fengwk
 */
public interface ThreadScopeAspect {

    Map<String, String> onEnter(SpanContext spanContext);

    void onExit(Map<String, String> store);

}
