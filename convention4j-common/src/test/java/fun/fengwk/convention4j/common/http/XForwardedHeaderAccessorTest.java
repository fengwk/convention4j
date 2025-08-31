package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.web.XForwardedHeaderAccessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("XForwardedHeaderAccessor 单元测试")
class XForwardedHeaderAccessorTest {

    private final String DIRECT_IP = "192.168.1.1";
    private final int DIRECT_PORT = 54321;
    private final String DIRECT_SCHEME = "http";
    private final String DIRECT_HOST = "api.internal.com";

    @Test
    @DisplayName("IP 解析: 应优先使用 X-Forwarded-For")
    void shouldResolveIpFromXForwardedFor() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-For", "1.1.1.1, 2.2.2.2"); // 客户端, 代理1
        headers.set("Forwarded", "for=3.3.3.3");

        XForwardedHeaderAccessor accessor = createAccessor(headers);

        assertThat(accessor.getClientIp()).isEqualTo("1.1.1.1");
    }

    @Test
    @DisplayName("IP 解析: XFF 不存在时，应使用 Forwarded 头")
    void shouldResolveIpFromForwarded() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Forwarded", "for=3.3.3.3;proto=https");

        XForwardedHeaderAccessor accessor = createAccessor(headers);

        assertThat(accessor.getClientIp()).isEqualTo("3.3.3.3");
    }

    @Test
    @DisplayName("IP 解析: Forwarded 头带引号和端口时也能正确解析")
    void shouldResolveIpFromQuotedForwarded() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Forwarded", "for=\"[::1]:54321\";proto=https");

        XForwardedHeaderAccessor accessor = createAccessor(headers);

        assertThat(accessor.getClientIp()).isEqualTo("[::1]:54321");
    }

    @Test
    @DisplayName("IP 解析: 无转发头时，应回退到直接连接 IP")
    void shouldFallbackToDirectIp() {
        HttpHeaders headers = new HttpHeaders();
        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getClientIp()).isEqualTo(DIRECT_IP);
    }

    @Test
    @DisplayName("协议解析: 应优先使用 X-Forwarded-Proto")
    void shouldResolveSchemeFromXForwardedProto() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "https");
        headers.set("Forwarded", "proto=http");

        XForwardedHeaderAccessor accessor = createAccessor(headers);

        assertThat(accessor.getScheme()).isEqualTo("https");
        assertThat(accessor.isSecure()).isTrue();
    }

    @Test
    @DisplayName("协议解析: X-Forwarded-Proto 不区分大小写")
    void shouldResolveSchemeCaseInsensitive() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "HTTPS");
        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getScheme()).isEqualTo("https");
    }

    @Test
    @DisplayName("协议解析: X-Forwarded-Proto 不存在时，应使用 Forwarded 头")
    void shouldResolveSchemeFromForwarded() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Forwarded", "proto=https");
        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getScheme()).isEqualTo("https");
    }

    @Test
    @DisplayName("协议解析: 无转发头时，应回退到直接连接协议")
    void shouldFallbackToDirectScheme() {
        HttpHeaders headers = new HttpHeaders();
        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getScheme()).isEqualTo(DIRECT_SCHEME);
        assertThat(accessor.isSecure()).isFalse();
    }

    @Test
    @DisplayName("Host 解析: 应优先使用 X-Forwarded-Host")
    void shouldResolveHostFromXForwardedHost() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Host", "user.example.com");
        headers.set("Forwarded", "host=proxy.example.com");
        headers.set(HttpHeaders.HOST, "api.internal.com");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getHost()).isEqualTo("user.example.com");
    }

    @Test
    @DisplayName("Host 解析: XFH 不存在时，应使用 Forwarded 头")
    void shouldResolveHostFromForwarded() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Forwarded", "host=proxy.example.com");
        headers.set(HttpHeaders.HOST, "api.internal.com");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getHost()).isEqualTo("proxy.example.com");
    }

    @Test
    @DisplayName("Host 解析: 无转发头时，应回退到 Host 头")
    void shouldFallbackToHostHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.HOST, "api.internal.com");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getHost()).isEqualTo("api.internal.com");
    }

    @Test
    @DisplayName("端口解析: 应优先使用 X-Forwarded-Port")
    void shouldResolvePortFromXForwardedPort() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Port", "8443");
        headers.set("Host", "user.example.com:8080");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getPort()).isEqualTo(8443);
    }

    @Test
    @DisplayName("端口解析: XFP 不存在时，应从 Host/XFH 中解析")
    void shouldResolvePortFromHost() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Host", "user.example.com:8080");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getPort()).isEqualTo(8080);
    }

    @Test
    @DisplayName("端口解析: 无端口头时，应根据 https 推断为 443")
    void shouldInferPort443ForHttps() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "https");

        XForwardedHeaderAccessor accessor = createAccessor(headers);
        assertThat(accessor.getPort()).isEqualTo(443);
    }

    @Test
    @DisplayName("端口解析: 无端口头时，应根据 http 推断为 80")
    void shouldInferPort80ForHttp() {
        HttpHeaders headers = new HttpHeaders();

        XForwardedHeaderAccessor accessor = createAccessor(headers, DIRECT_IP, DIRECT_PORT, "http", "hostonly.com");
        assertThat(accessor.getPort()).isEqualTo(80);
    }

    @Test
    @DisplayName("端口解析: 均无信息时，应回退到直接连接端口")
    void shouldFallbackToDirectPort() {
        HttpHeaders headers = new HttpHeaders();
        // 构造一个没有端口信息的 host 和 http 协议，使其无法推断
        XForwardedHeaderAccessor accessor = createAccessor(headers, DIRECT_IP, 54321, "ws", "hostonly.com");
        assertThat(accessor.getPort()).isEqualTo(54321);
    }

    @Test
    @DisplayName("端口解析: 格式错误的 X-Forwarded-Port 应被忽略并回退")
    void shouldIgnoreMalformedXForwardedPort() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Port", "not-a-port");

        XForwardedHeaderAccessor accessor = createAccessor(headers, DIRECT_IP, 12345, "http", "host.com");

        // 应该回退到直接连接的端口
        assertThat(accessor.getPort()).isEqualTo(12345);
    }

    @Test
    @DisplayName("Forwarded 复合头: 应能正确解析所有字段")
    void shouldParseCombinedForwardedHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Forwarded", "for=1.2.3.4; host=final.com:9000; proto=https");

        XForwardedHeaderAccessor accessor = createAccessor(headers);

        assertThat(accessor.getClientIp()).isEqualTo("1.2.3.4");
        assertThat(accessor.getScheme()).isEqualTo("https");
        assertThat(accessor.getHost()).isEqualTo("final.com:9000");
        assertThat(accessor.getPort()).isEqualTo(9000);
        assertThat(accessor.isSecure()).isTrue();
    }

    // Helper to create an accessor with default direct connection info
    private XForwardedHeaderAccessor createAccessor(HttpHeaders headers) {
        return createAccessor(headers, DIRECT_IP, DIRECT_PORT, DIRECT_SCHEME, DIRECT_HOST);
    }

    // Main helper to call the private constructor directly for logic testing
    private XForwardedHeaderAccessor createAccessor(HttpHeaders headers, String ip, int port, String scheme, String host) {
        // Using reflection to test the private constructor's logic directly
        try {
            var constructor = XForwardedHeaderAccessor.class.getDeclaredConstructor(HttpHeaders.class, String.class, int.class, String.class, String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(headers, ip, port, scheme, host);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate XForwardedHeaderAccessor for testing", e);
        }
    }

}