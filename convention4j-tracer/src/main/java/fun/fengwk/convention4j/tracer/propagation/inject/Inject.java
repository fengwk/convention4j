package fun.fengwk.convention4j.tracer.propagation.inject;

import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
public interface Inject<C> {

    Format<C> format();

    /**
     * @see io.opentracing.Tracer#inject(SpanContext, Format, Object)
     */
    void inject(SpanContext spanContext, C carrier);

}
