package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.common.web.XForwardedHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author fengwk
 */
@Slf4j
public class XForwardedHeaderGlobalFilter implements GlobalFilter, Ordered {

    private final XForwardedHeaderProperties xForwardHeaderProperties;
    private final String gatewayViaIdentifier;

    public XForwardedHeaderGlobalFilter(XForwardedHeaderProperties xForwardHeaderProperties,
                                        Environment environment) {
        Assert.notNull(xForwardHeaderProperties, "xForwardHeaderProperties must not be null");
        this.xForwardHeaderProperties = xForwardHeaderProperties;
        this.gatewayViaIdentifier = environment.getProperty(
            "spring.application.name",
            "spring-cloud-gateway"
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果未启用，则直接跳过
        if (!xForwardHeaderProperties.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpRequest.Builder requestBuilder = originalRequest.mutate();
        if (xForwardHeaderProperties.isOriginalUriEnabled()) {
            addOriginalUriHeader(requestBuilder, originalRequest);
        }
        if (xForwardHeaderProperties.isXForwardedEnabled()) {
            addXForwardedHeaders(requestBuilder, originalRequest);
        }
        if (xForwardHeaderProperties.isForwardedEnabled()) {
            addForwardedHeader(requestBuilder, originalRequest);
        }
        if (xForwardHeaderProperties.isViaEnabled()) {
            addViaHeader(requestBuilder, originalRequest);
        }
        if (xForwardHeaderProperties.isRealPortEnabled()) {
            addRealPortHeader(requestBuilder, originalRequest);
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    private void addRealPortHeader(ServerHttpRequest.Builder requestBuilder, ServerHttpRequest originalRequest) {
        String realPort = originalRequest.getHeaders().getFirst(XForwardedHeader.X_REAL_PORT.getName());
        if (!StringUtils.hasText(realPort) && originalRequest.getRemoteAddress() != null) {
            int remotePort = originalRequest.getRemoteAddress().getPort();
            realPort = String.valueOf(remotePort);
        }
        requestBuilder.header(XForwardedHeader.X_REAL_PORT.getName(), realPort);
    }

    /**
     * 添加 X-Original-URI 头部，记录被网关改写前的原始请求URI
     */
    private void addOriginalUriHeader(ServerHttpRequest.Builder requestBuilder, ServerHttpRequest originalRequest) {
        String originalUri = originalRequest.getHeaders().getFirst(XForwardedHeader.X_ORIGINAL_URI.getName());
        if (!StringUtils.hasText(originalUri) ) {
            originalUri = originalRequest.getURI().getRawPath();
            if (StringUtils.hasText(originalRequest.getURI().getRawQuery())) {
                originalUri += "?" + originalRequest.getURI().getRawQuery();
            }
        }
        requestBuilder.header(XForwardedHeader.X_ORIGINAL_URI.getName(), originalUri);

    }

    /**
     * 添加 X-Forwarded-* 系列头部
     */
    private void addXForwardedHeaders(ServerHttpRequest.Builder requestBuilder, ServerHttpRequest request) {
        // 1. 处理 X-Forwarded-For
        String clientIp = getClientIp(request);
        String xffHeader = request.getHeaders().getFirst(XForwardedHeader.X_FORWARDED_FOR.getName());
        String newXffHeader = (xffHeader == null) ? clientIp : xffHeader + ", " + clientIp;
        requestBuilder.header(XForwardedHeader.X_FORWARDED_FOR.getName(), newXffHeader);
        // 2. 处理 X-Forwarded-Proto
        String scheme = request.getURI().getScheme();
        requestBuilder.header(XForwardedHeader.X_FORWARDED_PROTO.getName(), scheme);
        // 3. 处理 X-Forwarded-Host
        String host = request.getHeaders().getFirst(XForwardedHeader.HOST.getName());
        if (host != null) {
            requestBuilder.header(XForwardedHeader.X_FORWARDED_HOST.getName(), host);
        }
        // 4. 处理 X-Forwarded-Port
        String port = getPort(request);
        requestBuilder.header(XForwardedHeader.X_FORWARDED_PORT.getName(), port);
    }

    /**
     * 添加 RFC 7239 标准的 Forwarded 头部
     */
    private void addForwardedHeader(ServerHttpRequest.Builder requestBuilder, ServerHttpRequest request) {
        String clientIp = getClientIp(request);
        String formattedIp = formatIpForForwardedHeader(clientIp);
        String proto = request.getURI().getScheme();
        String host = request.getHeaders().getFirst(XForwardedHeader.HOST.getName());

        // 构建当前代理节点的信息
        String currentForwarded = String.format("for=%s;proto=%s;host=%s", formattedIp, proto, host);

        String existingForwarded = request.getHeaders().getFirst(XForwardedHeader.FORWARDED.getName());
        String newForwardedHeader = (existingForwarded == null) ? currentForwarded : existingForwarded + ", " + currentForwarded;

        requestBuilder.header(XForwardedHeader.FORWARDED.getName(), newForwardedHeader);
    }

    /**
     * 添加 RFC 7230 Via 头部, 标识请求经过了本网关。
     */
    private void addViaHeader(ServerHttpRequest.Builder requestBuilder, ServerHttpRequest request) {
        // ServerHttpRequest API 不提供直接获取HTTP版本的方法。
        // 根据RFC 7230，使用一个假名(pseudonym)来标识代理是完全合规的。
        String existingVia = request.getHeaders().getFirst(XForwardedHeader.VIA.getName());
        String newViaHeader = (existingVia == null) ? gatewayViaIdentifier : existingVia + ", " + gatewayViaIdentifier;

        requestBuilder.header(XForwardedHeader.VIA.getName(), newViaHeader);
    }

    /**
     * 从请求中安全地获取客户端IP地址。
     *
     * @param request ServerHttpRequest
     * @return 客户端IP地址，如果无法获取则返回 "unknown"。
     */
    private String getClientIp(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
            .map(InetSocketAddress::getAddress)
            .map(java.net.InetAddress::getHostAddress)
            .orElse("unknown");
    }

    /**
     * 从请求中获取端口号，如果URI中没有则根据协议推断默认端口。
     *
     * @param request ServerHttpRequest
     * @return 端口号字符串。
     */
    private String getPort(ServerHttpRequest request) {
        int port = request.getURI().getPort();
        if (port != -1) {
            return String.valueOf(port);
        }
        return "https".equalsIgnoreCase(request.getURI().getScheme()) ? "443" : "80";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    private String formatIpForForwardedHeader(String ip) {
        // 如果是IPv6地址，需要用引号和方括号包围
        if (ip != null && ip.contains(":")) {
            return String.format("\"[%s]\"", ip);
        }
        return ip;
    }

}