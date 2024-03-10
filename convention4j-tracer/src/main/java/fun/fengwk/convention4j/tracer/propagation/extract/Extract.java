package fun.fengwk.convention4j.tracer.propagation.extract;

import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
public interface Extract<C> {

    Format<C> format();

    /**
     * @see io.opentracing.Tracer#extract(Format, Object)
     */
    SpanContext extract(C carrier);

}
