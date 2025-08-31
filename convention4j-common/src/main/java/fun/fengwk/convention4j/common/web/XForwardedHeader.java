package fun.fengwk.convention4j.common.web;

/**
 * @author fengwk
 */
public enum XForwardedHeader {

    /**
     * HTTP/1.1协议标准头部，非"X-"头，但与代理上下文紧密相关。
     * 作用：指示客户端请求的原始目标主机名和端口。这是实现虚拟主机托管的基础。
     * 值的数据结构：一个主机名，可选地后跟一个冒号和端口号。
     * 例子：
     * <pre>
     * Host: www.example.com
     * Host: api.example.com:8443
     * </pre>
     * 注意：此头部可能在代理转发时被修改为后端服务的内部地址，因此需要依赖 X-Forwarded-Host 来获取原始值。
     */
    HOST("Host"),

    /**
     * 客户端的实际端口
     */
    X_REAL_PORT("X-Real-Port"),

    /**
     * 识别客户端与代理之间使用的原始协议（http或https）。
     * 作用：当代理服务器进行TLS终止（TLS Termination）时，后端服务收到的总是普通HTTP请求。此头部让后端服务能够判断原始请求是否是安全的（HTTPS），从而做出相应处理，例如设置Cookie的'Secure'标志或生成正确的https链接。
     * 值的数据结构：一个字符串，通常是 "http" 或 "https"。
     * 例子：
     * <pre>
     * X-Forwarded-Proto: https
     * </pre>
     */
    X_FORWARDED_PROTO("X-Forwarded-Proto"),

    /**
     * 一个事实上的标准，用于识别通过HTTP代理或负载均衡器连接到Web服务器的客户端的原始IP地址。
     * 作用：这是识别请求链中所有IP地址的标准方法，包括原始客户端和中间各级代理。
     * 值的数据结构：一个逗号分隔的IP地址列表。最左边的IP通常是原始客户端的IP。
     * 例子：
     * <pre>
     * // 单级代理
     * X-Forwarded-For: 203.0.113.195
     * // 多级代理
     * X-Forwarded-For: 203.0.113.195, 198.51.100.10, 10.0.0.1
     * </pre>
     * 安全注意：此头部的值可能被恶意客户端伪造，因此在处理时应只信任由您的基础设施（如负载均衡器）添加的部分。
     */
    X_FORWARDED_FOR("X-Forwarded-For"),

    /**
     * 识别客户端请求的原始Host头部。
     * 作用：当代理将请求转发到后端时，可能会修改标准的`Host`头部。此头部保留了原始值，使得后端应用可以生成正确的、面向公网的绝对URL。
     * 值的数据结构：一个主机名，可选地后跟一个冒号和端口号。
     * 例子：
     * <pre>
     * X-Forwarded-Host: www.example.com
     * </pre>
     */
    X_FORWARDED_HOST("X-Forwarded-Host"),

    /**
     * 识别客户端与代理之间使用的原始目标端口。
     * 作用：补充 X-Forwarded-Host，在客户端使用非标准端口（如80或443之外）访问时尤其重要。
     * 值的数据结构：一个端口号。
     * 例子：
     * <pre>
     * X-Forwarded-Port: 8443
     * </pre>
     */
    X_FORWARDED_PORT("X-Forwarded-Port"),

    /**
     * 保存了在代理服务器上进行任何URL重写之前的原始请求URI。
     * 作用：如果代理（如Nginx）配置了rewrite规则，后端服务将看到重写后的URI。此头部可以让后端知道用户最初请求的地址是什么，对于日志记录和某些路由逻辑很有用。
     * 值的数据结构：一个URL路径，通常以'/'开头，可能包含查询字符串。
     * 例子：
     * <pre>
     * X-Original-URI: /old-path/resource?id=123
     * </pre>
     */
    X_ORIGINAL_URI("X-Original-URI"),

    /**
     * 由RFC 7239定义的标准化头部，旨在替代所有非标准的 X-Forwarded-* 头部。
     * 作用：用一个结构化的方式提供关于代理转发请求的元信息。
     * 值的数据结构：一个分号分隔的键值对列表。常见的键有 'by', 'for', 'host', 'proto'。
     * 例子：
     * <pre>
     * Forwarded: for=203.0.113.195;proto=https;host=www.example.com;by=198.51.100.10
     * </pre>
     * 推荐：新系统应优先考虑处理此头部，但为了向后兼容，仍需支持 X-Forwarded-*。
     */
    FORWARDED("Forwarded"),

    /**
     * 由RFC 7230定义，是一个标准的HTTP头部，提供关于请求路径上的中间代理和协议版本的信息。
     * 作用：主要用于请求的追踪、环路检测和协议能力协商。
     * 值的数据结构：一个逗号分隔的代理节点列表。
     * 例子：
     * <pre>
     * Via: 1.1 nginx, 1.1 my-internal-proxy:8080
     * </pre>
     */
    VIA("Via"),

    ;

    private final String name;

    XForwardedHeader(String name) {
        this.name = name;
    }

    /**
     * 获取此枚举常量对应的HTTP头部名称。
     *
     * @return HTTP头部的字符串名称，例如 "X-Forwarded-For"。
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}