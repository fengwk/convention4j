package fun.fengwk.convention4j.springboot.starter.rocketmq;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class RocketMQTracerContext {

    static final String TRACER_CONTEXT = "TRACER_CONTEXT";

    private final Span span;
    private final Scope scope;

}
