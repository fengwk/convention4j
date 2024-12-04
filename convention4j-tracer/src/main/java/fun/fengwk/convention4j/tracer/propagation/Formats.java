package fun.fengwk.convention4j.tracer.propagation;

import io.opentracing.propagation.Format;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author fengwk
 */
public class Formats {

    public static final Format<HttpServletRequest> HTTP_SERVLET_REQUEST_EXTRACT = new Format<>() {};

}
