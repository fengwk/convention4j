package fun.fengwk.convention4j.springboot.starter.webflux.context;

import fun.fengwk.convention4j.springboot.starter.webflux.tracer.WebFluxSpan;
import io.opentracing.Scope;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class TraceInfo {

    private final WebFluxSpan webFluxSpan;
    private final Scope scope;

}
