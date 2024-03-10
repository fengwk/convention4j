package fun.fengwk.convention4j.tracer;

import io.opentracing.tag.Tag;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class TagValue<T> {

    private final Tag<T> tag;
    private final T value;

}
