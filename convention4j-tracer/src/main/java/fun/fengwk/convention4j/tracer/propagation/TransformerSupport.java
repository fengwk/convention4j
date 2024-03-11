package fun.fengwk.convention4j.tracer.propagation;

import io.opentracing.propagation.Format;

/**
 * @author fengwk
 */
public interface TransformerSupport<C> {

    /**
     * @see Format
     */
    Format<C> format();

}
