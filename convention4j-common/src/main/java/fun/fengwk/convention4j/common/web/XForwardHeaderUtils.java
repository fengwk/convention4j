package fun.fengwk.convention4j.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

/**
 * @author fengwk
 */
public class XForwardHeaderUtils {

    private XForwardHeaderUtils() {}

    public static XForwardedHeaderAccessor from(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator().forEachRemaining(name ->
            headers.add(name, request.getHeader(name)));

        return new XForwardedHeaderAccessor(
            headers,
            request.getRemoteAddr(),
            request.getRemotePort(),
            request.getScheme(),
            request.getHeader(HttpHeaders.HOST)
        );
    }

}
