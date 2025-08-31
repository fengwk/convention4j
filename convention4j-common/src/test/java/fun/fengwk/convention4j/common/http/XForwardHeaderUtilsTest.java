package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.web.XForwardHeaderUtils;
import fun.fengwk.convention4j.common.web.XForwardedHeaderAccessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("XForwardHeaderUtils 单元测试")
class XForwardHeaderUtilsTest {

    @Test
    @DisplayName("from(HttpServletRequest): 应正确抽取信息")
    void testFromHttpServletRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        request.setRemotePort(12345);
        request.setScheme("http");
        request.addHeader(HttpHeaders.HOST, "internal.host");
        request.addHeader("X-Forwarded-For", "34.35.36.37");

        XForwardedHeaderAccessor accessor = XForwardHeaderUtils.from(request);

        assertThat(accessor.getDirectRemoteAddr()).isEqualTo("10.0.0.1");
        assertThat(accessor.getDirectRemotePort()).isEqualTo(12345);
        assertThat(accessor.getDirectScheme()).isEqualTo("http");
        assertThat(accessor.getClientIp()).isEqualTo("34.35.36.37"); // 确认头部被正确传递
        assertThat(accessor.getHost()).isEqualTo("internal.host");
    }

    @Test
    @DisplayName("无论转发头如何，getDirect* 方法都应返回直接连接信息")
    void directMethodsShouldIgnoreForwardingHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.10.20");
        request.setRemotePort(55555);
        request.setScheme("http");
        request.addHeader("X-Forwarded-For", "1.1.1.1");
        request.addHeader("X-Forwarded-Proto", "https");

        XForwardedHeaderAccessor accessor = XForwardHeaderUtils.from(request);

        assertThat(accessor.getDirectRemoteAddr()).isEqualTo("192.168.10.20");
        assertThat(accessor.getDirectRemotePort()).isEqualTo(55555);
        assertThat(accessor.getDirectScheme()).isEqualTo("http");
    }

}