package fun.fengwk.convention4j.springboot.starter.webflux.util;

import fun.fengwk.convention4j.common.web.XForwardedHeaderAccessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("ReactiveXForwardHeaderUtils 单元测试")
class ReactiveXForwardHeaderUtilsTest {

    @Test
    @DisplayName("from(ServerHttpRequest): 应正确抽取信息")
    void testFromServerHttpRequest() {
        ServerHttpRequest request = MockServerHttpRequest
            .get("http://internal.host/path")
            .remoteAddress(new InetSocketAddress("10.0.0.2", 23456))
            .header("X-Forwarded-For", "44.45.46.47")
            .build();

        XForwardedHeaderAccessor accessor = ReactiveXForwardHeaderUtils.from(request);

        assertThat(accessor.getDirectRemoteAddr()).isEqualTo("10.0.0.2");
        assertThat(accessor.getDirectRemotePort()).isEqualTo(23456);
        assertThat(accessor.getDirectScheme()).isEqualTo("http");
        assertThat(accessor.getClientIp()).isEqualTo("44.45.46.47");
    }

    @Test
    @DisplayName("from(ServerRequest): 应正确抽取信息并处理 Optional")
    void testFromServerRequest() {
        ServerRequest mockServerRequest = Mockito.mock(ServerRequest.class);
        ServerRequest.Headers mockHeaders = Mockito.mock(ServerRequest.Headers.class);

        when(mockServerRequest.remoteAddress()).thenReturn(Optional.of(new InetSocketAddress("10.0.0.3", 34567)));
        when(mockServerRequest.uri()).thenReturn(URI.create("https://final.host/path"));
        when(mockServerRequest.headers()).thenReturn(mockHeaders);
        when(mockHeaders.asHttpHeaders()).thenReturn(new HttpHeaders() {{
            add("X-Forwarded-Proto", "https");
        }});
        // 关键：测试 Optional<String> 的处理
        when(mockHeaders.firstHeader(HttpHeaders.HOST)).thenReturn("from.optional.host");

        XForwardedHeaderAccessor accessor = ReactiveXForwardHeaderUtils.from(mockServerRequest);

        assertThat(accessor.getDirectRemoteAddr()).isEqualTo("10.0.0.3");
        assertThat(accessor.getDirectRemotePort()).isEqualTo(34567);
        assertThat(accessor.getDirectScheme()).isEqualTo("https"); // from URI
        assertThat(accessor.getScheme()).isEqualTo("https"); // from Header
        assertThat(accessor.getHost()).isEqualTo("from.optional.host");
    }

    @Test
    @DisplayName("from(ServerRequest): 当 remoteAddress 为空时应正常工作")
    void testFromServerRequestWithEmptyRemoteAddress() {
        ServerRequest mockServerRequest = Mockito.mock(ServerRequest.class);
        ServerRequest.Headers mockHeaders = Mockito.mock(ServerRequest.Headers.class);

        when(mockServerRequest.remoteAddress()).thenReturn(Optional.empty());
        when(mockServerRequest.uri()).thenReturn(URI.create("http://host/path"));
        when(mockServerRequest.headers()).thenReturn(mockHeaders);
        when(mockHeaders.asHttpHeaders()).thenReturn(new HttpHeaders());
        when(mockHeaders.firstHeader(HttpHeaders.HOST)).thenReturn(null);

        XForwardedHeaderAccessor accessor = ReactiveXForwardHeaderUtils.from(mockServerRequest);

        assertThat(accessor.getDirectRemoteAddr()).isEqualTo(null);
        assertThat(accessor.getDirectRemotePort()).isEqualTo(-1);
    }

}