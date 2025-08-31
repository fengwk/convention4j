package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.common.web.XForwardedHeaderAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author fengwk
 */
public class ReactiveXForwardHeaderUtils {

    private ReactiveXForwardHeaderUtils() {
    }

    public static XForwardedHeaderAccessor from(ServerHttpRequest request) {
        String directHost = Optional.ofNullable(request.getHeaders().getHost())
            .map(InetSocketAddress::getHostString)
            .orElse(null);

        String remoteAddr = Optional.ofNullable(request.getRemoteAddress())
            .map(InetSocketAddress::getHostString)
            .orElse(null);

        int remotePort = Optional.ofNullable(request.getRemoteAddress())
            .map(InetSocketAddress::getPort)
            .orElse(-1);

        String directUri = request.getURI().getRawPath();
        if (StringUtils.hasText(request.getURI().getRawQuery())) {
            directUri += "?" + request.getURI().getRawQuery();
        }

        return new XForwardedHeaderAccessor(
            request.getHeaders(),
            remoteAddr,
            remotePort,
            request.getURI().getScheme(),
            directHost,
            request.getURI().getPort(),
            directUri
        );
    }

    public static XForwardedHeaderAccessor from(ServerRequest request) {
        String directHost = request.headers().firstHeader(HttpHeaders.HOST);

        String remoteAddr = request.remoteAddress()
            .map(InetSocketAddress::getHostString)
            .orElse(null);

        int remotePort = request.remoteAddress()
            .map(InetSocketAddress::getPort)
            .orElse(-1);

        String directUri = request.uri().getRawPath();
        if (StringUtils.hasText(request.uri().getRawQuery())) {
            directUri += "?" + request.uri().getRawQuery();
        }

        return new XForwardedHeaderAccessor(
            request.headers().asHttpHeaders(),
            remoteAddr,
            remotePort,
            request.uri().getScheme(),
            directHost,
            request.uri().getPort(),
            directUri
        );
    }

}
