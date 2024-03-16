package fun.fengwk.convention4j.tracer.propagation;

/**
 * @author fengwk
 */
public class PropagationConstants {

    public static final String TRACE_ID_HTTP_HEADER_NAME = "X-Trace-ID";
    public static final String SPAN_ID_HTTP_HEADER_NAME = "X-Span-ID";
    public static final String BAGGAGE_HEADER_NAME = "X-Baggage";
    public static final String MESSAGE_SPAN_CONTEXT_KEY = "_spanContext";

}
