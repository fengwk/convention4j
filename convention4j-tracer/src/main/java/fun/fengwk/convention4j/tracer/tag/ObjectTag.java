package fun.fengwk.convention4j.tracer.tag;

import io.opentracing.Span;
import io.opentracing.tag.AbstractTag;

/**
 * @author fengwk
 */
public class ObjectTag<T> extends AbstractTag<T> {

    public ObjectTag(String tagKey) {
        super(tagKey);
    }

    @Override
    public void set(Span span, T tagValue) {
        span.setTag(this, tagValue);
    }

}
