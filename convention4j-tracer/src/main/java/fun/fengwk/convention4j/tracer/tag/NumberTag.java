package fun.fengwk.convention4j.tracer.tag;

import io.opentracing.Span;
import io.opentracing.tag.AbstractTag;

/**
 * @author fengwk
 */
public class NumberTag extends AbstractTag<Number> {

    public NumberTag(String tagKey) {
        super(tagKey);
    }

    @Override
    public void set(Span span, Number tagValue) {
        span.setTag(this, tagValue);
    }

}
