package fun.fengwk.convention4j.tracer.finisher;

import fun.fengwk.convention4j.tracer.SpanImpl;
import io.opentracing.Span;

/**
 * @author fengwk
 */
public interface SpanFinisher {

    /**
     * @see Span#finish()
     */
    void finish(SpanImpl span, long finishMicros);

}
