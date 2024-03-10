package fun.fengwk.convention4j.tracer;

import io.opentracing.SpanContext;
import lombok.Data;

import java.util.Objects;

/**
 * @author fengwk
 */
@Data
public class Reference {

    private final String referenceType;
    private final SpanContext spanContext;

    public Reference(String referenceType, SpanContext spanContext) {
        this.referenceType = Objects.requireNonNull(referenceType, "Reference type must not be null");
        this.spanContext = Objects.requireNonNull(spanContext, "Span context must not be null");
    }

}
