package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import fun.fengwk.convention4j.tracer.propagation.PropagationConstants;

/**
 * @author fengwk
 */
public enum TracerXHeader {

    X_TRACE_ID(PropagationConstants.TRACE_ID_HTTP_HEADER_NAME),
    X_SPAN_ID(PropagationConstants.SPAN_ID_HTTP_HEADER_NAME),
    X_BAGGAGE(PropagationConstants.BAGGAGE_HEADER_NAME),
    ;

    private final String name;

    TracerXHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
