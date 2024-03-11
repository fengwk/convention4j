package fun.fengwk.convention4j.tracer.propagation.extract;

import fun.fengwk.convention4j.tracer.propagation.TransformerSupport;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
public interface Extract<C> extends TransformerSupport<C> {

    /**
     * @see io.opentracing.Tracer#extract(Format, Object)
     */
    SpanContext extract(C carrier);

}
