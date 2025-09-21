package fun.fengwk.convention4j.common.web;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * 一个健壮的工具类，用于从 HTTP 请求中解析客户端的真实 IP、协议、Host 和端口，
 * 优先考虑常见的反向代理/负载均衡器设置的 'X-Forwarded-*' 和 'Forwarded' 头部。
 * <p>
 * 它遵循以下标准的优先级顺序：
 * <ul>
 *   <li><b>IP:</b> {@code X-Forwarded-For} > {@code Forwarded} > 直接连接的远程地址</li>
 *   <li><b>协议:</b> {@code X-Forwarded-Proto} > {@code Forwarded} > 直接连接的协议</li>
 *   <li><b>Host:</b> {@code X-Forwarded-Host} > {@code Forwarded} > {@code Host} header > 直接连接的 Host</li>
 *   <li><b>端口:</b> {@code X-Forwarded-Port} > Host/XFH 中的端口 > 根据协议推断 (443/80) > 直接连接的端口</li>
 * </ul>
 * <p>
 * 这个类是不可变的，并且是线程安全的。
 *
 * @author fengwk
 */
@Slf4j
public final class XForwardedHeaderAccessor {

    @Getter
    private final String clientIp;
    @Getter
    private final int clientPort;
    @Getter
    private final String scheme;
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final String originalUri;
    @Getter
    private final String via;

    @Getter
    private final String directRemoteAddr;
    @Getter
    private final int directRemotePort;
    @Getter
    private final String directScheme;

    public XForwardedHeaderAccessor(
        HttpHeaders headers,
        String directRemoteAddr,
        int directRemotePort,
        String directScheme,
        String directHost,
        int directPort,
        String directUri) {
        this.directRemoteAddr = directRemoteAddr;
        this.directRemotePort = directRemotePort;
        this.directScheme = directScheme;

        this.clientIp = resolveClientIp(headers, directRemoteAddr);
        this.clientPort = resolveClientPort(headers, directRemotePort);
        this.scheme = resolveScheme(headers, directScheme);
        this.host = resolveHost(headers, directHost);
        this.port = resolvePort(headers, this.scheme, this.host, directPort);
        this.originalUri = resolveOriginalUri(headers, directUri);
        this.via = resolveVia(headers);
    }

    // --- 解析逻辑 ---

    private int resolveClientPort(HttpHeaders headers, int fallback) {
        String xrp = headers.getFirst(XForwardedHeader.X_REAL_PORT.getName());
        if (StringUtils.hasText(xrp)) {
            try {
                return  Integer.parseInt(xrp);
            } catch (NumberFormatException ex) {
                log.debug("Invalid X-Real-Port, xrp: {}", xrp, ex);
            }
        }
        return fallback;
    }

    private String resolveOriginalUri(HttpHeaders headers, String fallback) {
        String originalUri = headers.getFirst(XForwardedHeader.X_ORIGINAL_URI.getName());
        if (StringUtils.hasText(originalUri)) {
            return originalUri;
        }
        return fallback;
    }

    private String resolveVia(HttpHeaders headers) {
        String via = headers.getFirst(XForwardedHeader.VIA.getName());
        if (StringUtils.hasText(via)) {
            return via;
        }
        return null;
    }

    private String resolveClientIp(HttpHeaders headers, String fallback) {
        String xff = headers.getFirst(XForwardedHeader.X_FORWARDED_FOR.getName());
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        String forwarded = headers.getFirst(XForwardedHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String ip = getForwardedHeaderValue(forwarded, "for");
            if (ip != null) {
                return ip;
            }
        }
        return fallback;
    }

    private String resolveScheme(HttpHeaders headers, String fallback) {
        String xfp = headers.getFirst(XForwardedHeader.X_FORWARDED_PROTO.getName());
        if (StringUtils.hasText(xfp)) {
            return xfp.split(",")[0].trim().toLowerCase();
        }
        String forwarded = headers.getFirst(XForwardedHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String proto = getForwardedHeaderValue(forwarded, "proto");
            if (proto != null) {
                return proto.toLowerCase();
            }
        }
        return fallback;
    }

    private String resolveHost(HttpHeaders headers, String fallback) {
        String xfh = headers.getFirst(XForwardedHeader.X_FORWARDED_HOST.getName());
        if (StringUtils.hasText(xfh)) {
            return xfh.split(",")[0].trim();
        }
        String forwarded = headers.getFirst(XForwardedHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String host = getForwardedHeaderValue(forwarded, "host");
            if (host != null) {
                return host;
            }
        }
        return fallback;
    }

    /**
     * 解析端口号。
     * 此版本修复了当 'X-Forwarded-Port' 格式错误时，错误地回退到 scheme 推断端口的问题。
     * 正确的逻辑是：如果一个高优先级的头部（如 X-Forwarded-Port）存在但无效，
     * 应该立即停止解析并使用最可靠的备用值（直接连接端口），而不是进行不安全的推断。
     */
    private int resolvePort(HttpHeaders headers, String resolvedScheme, String resolvedHost, int fallback) {
        // 优先使用 X-Forwarded-Port
        String portStr = headers.getFirst(XForwardedHeader.X_FORWARDED_PORT.getName());
        if (StringUtils.hasText(portStr)) {
            try {
                return Integer.parseInt(portStr.split(",")[0].trim());
            } catch (NumberFormatException ex) {
                log.debug("Invalid X-Forwarded-Port, xfr: {}", portStr);
            }
        }

        // 如果解析出的 Host 包含端口，则使用它
        if (resolvedHost != null && resolvedHost.contains(":")) {
            try {
                // 支持 IPv6 地址格式 [::1]:port
                return Integer.parseInt(resolvedHost.substring(resolvedHost.lastIndexOf(':') + 1));
            } catch (NumberFormatException ignored) {
                // Host 中的端口也可能是无效的，忽略并继续
            }
        }

        if (fallback != -1) {
            return fallback;
        }

        if ("https".equalsIgnoreCase(resolvedScheme)) {
            return 443;
        } else if ("http".equalsIgnoreCase(resolvedScheme)) {
            return 80;
        }

        return -1;
    }

    private String getForwardedHeaderValue(String forwardedHeader, String key) {
        String prefix = key + "=";
        // 规范要求只处理第一个代理节点的信息
        String firstHop = forwardedHeader.split(",")[0].trim();
        for (String part : firstHop.split(";")) {
            String trimmedPart = part.trim();
            if (trimmedPart.toLowerCase().startsWith(prefix)) {
                // 移除 key 前缀和可能存在的引号
                return trimmedPart.substring(prefix.length()).replace("\"", "").trim();
            }
        }
        return null;
    }

    /**
     * 判断解析后的协议是否为 https。
     */
    public boolean isSecure() {
        return "https".equalsIgnoreCase(getScheme());
    }

}

