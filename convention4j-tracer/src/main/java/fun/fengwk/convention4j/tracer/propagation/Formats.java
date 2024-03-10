package fun.fengwk.convention4j.tracer.propagation;

import io.opentracing.propagation.Format;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.rocketmq.common.message.Message;

/**
 * @author fengwk
 */
public class Formats {

    public static final Format<HttpServletRequest> HTTP_SERVLET_REQUEST_EXTRACT = new Format<>() {};
    public static final Format<Message> MESSAGE_INJECT = new Format<>() {};
    public static final Format<Message> MESSAGE_EXTRACT = new Format<>() {};
    public static final Format<Object> TTL_INJECT = new Format<>() {};
    public static final Format<Object> TTL_EXTRACT = new Format<>() {};

}
