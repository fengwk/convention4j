package fun.fengwk.convention4j.common.web;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
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
public final class XForwardedHeaderAccessor {

    private final String clientIp;
    private final String scheme;
    private final String host;
    private final int port;

    private final String directRemoteAddr;
    private final int directRemotePort;
    private final String directScheme;

    public XForwardedHeaderAccessor(
        HttpHeaders headers,
        String directRemoteAddr,
        int directRemotePort,
        String directScheme,
        @Nullable String directHost) {

        this.directRemoteAddr = directRemoteAddr;
        this.directRemotePort = directRemotePort;
        this.directScheme = directScheme;

        this.clientIp = resolveClientIp(headers, directRemoteAddr);
        this.scheme = resolveScheme(headers, directScheme);
        this.host = resolveHost(headers, directHost);
        this.port = resolvePort(headers, this.scheme, this.host, directRemotePort);
    }

    // --- 解析逻辑 ---

    private String resolveClientIp(HttpHeaders headers, String fallback) {
        String xff = headers.getFirst(XForwardHeader.X_FORWARDED_FOR.getName());
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        String forwarded = headers.getFirst(XForwardHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String ip = getForwardedHeaderValue(forwarded, "for");
            if (ip != null) {
                return ip;
            }
        }
        return fallback;
    }

    private String resolveScheme(HttpHeaders headers, String fallback) {
        String xfp = headers.getFirst(XForwardHeader.X_FORWARDED_PROTO.getName());
        if (StringUtils.hasText(xfp)) {
            return xfp.split(",")[0].trim().toLowerCase();
        }
        String forwarded = headers.getFirst(XForwardHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String proto = getForwardedHeaderValue(forwarded, "proto");
            if (proto != null) {
                return proto.toLowerCase();
            }
        }
        return fallback;
    }

    private String resolveHost(HttpHeaders headers, @Nullable String fallback) {
        String xfh = headers.getFirst(XForwardHeader.X_FORWARDED_HOST.getName());
        if (StringUtils.hasText(xfh)) {
            return xfh.split(",")[0].trim();
        }
        String forwarded = headers.getFirst(XForwardHeader.FORWARDED.getName());
        if (StringUtils.hasText(forwarded)) {
            String host = getForwardedHeaderValue(forwarded, "host");
            if (host != null) {
                return host;
            }
        }
        return fallback;
    }

    /**
     * [已修正] 解析端口号。
     * 此版本修复了当 'X-Forwarded-Port' 格式错误时，错误地回退到 scheme 推断端口的问题。
     * 正确的逻辑是：如果一个高优先级的头部（如 X-Forwarded-Port）存在但无效，
     * 应该立即停止解析并使用最可靠的备用值（直接连接端口），而不是进行不安全的推断。
     */
    private int resolvePort(HttpHeaders headers, String resolvedScheme, @Nullable String resolvedHost, int fallback) {
        // 1. 优先使用 X-Forwarded-Port
        String portStr = headers.getFirst(XForwardHeader.X_FORWARDED_PORT.getName());
        if (StringUtils.hasText(portStr)) {
            try {
                return Integer.parseInt(portStr.split(",")[0].trim());
            } catch (NumberFormatException ignored) {
                // [核心修复]
                // 头部存在但无效，是一个危险信号，表明代理配置可能出错。
                // 此时最安全的做法是直接使用最终的 fallback，而不是继续进行推断。
                return fallback;
            }
        }

        // 2. 如果解析出的 Host 包含端口，则使用它
        if (resolvedHost != null && resolvedHost.contains(":")) {
            try {
                // 支持 IPv6 地址格式 [::1]:port
                return Integer.parseInt(resolvedHost.substring(resolvedHost.lastIndexOf(':') + 1));
            } catch (NumberFormatException ignored) {
                // Host 中的端口也可能是无效的，忽略并继续
            }
        }

        // 3. 只有在完全没有指定端口的情况下，才根据 scheme 推断默认端口
        if ("https".equalsIgnoreCase(resolvedScheme)) {
            return 443;
        }
        if ("http".equalsIgnoreCase(resolvedScheme)) {
            return 80;
        }

        // 4. 回退到直接连接的端口
        return fallback;
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

    // --- 公共 Getters ---

    /**
     * 获取解析后的客户端真实 IP 地址。
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 获取解析后的客户端真实请求协议 (scheme)。
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * 获取解析后的客户端真实请求 Host。
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取解析后的客户端真实请求端口。
     */
    public int getPort() {
        return port;
    }

    /**
     * 判断解析后的协议是否为 https。
     */
    public boolean isSecure() {
        return "https".equalsIgnoreCase(getScheme());
    }

    /**
     * 获取直接连接到本服务器的客户端地址（通常是上一级代理的 IP）。
     */
    public String getDirectRemoteAddr() {
        return directRemoteAddr;
    }

    /**
     * 获取直接连接到本服务器的客户端端口。
     */
    public int getDirectRemotePort() {
        return directRemotePort;
    }

    /**
     * 获取直接连接的协议。
     */
    public String getDirectScheme() {
        return directScheme;
    }
}

