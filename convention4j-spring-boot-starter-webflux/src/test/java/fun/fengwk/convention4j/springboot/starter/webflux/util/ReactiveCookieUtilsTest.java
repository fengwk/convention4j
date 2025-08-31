package fun.fengwk.convention4j.springboot.starter.webflux.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author fengwk
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CookieUtils 单元测试")
public class ReactiveCookieUtilsTest {

    private static final String COOKIE_NAME = "test-cookie";
    private static final String COOKIE_VALUE = "test-value";
    private static final String CUSTOM_PATH = "/custom";

    @Mock
    private ServerHttpRequest serverHttpRequest;
    @Mock
    private ServerRequest serverRequest;
    @Mock
    private ServerHttpResponse serverHttpResponse;
    @Mock
    private ServerResponse.BodyBuilder bodyBuilder;

    /*
     * ===================================================================
     * getCookieValue Tests
     * ===================================================================
     */
    @Test
    @DisplayName("getCookieValue(ServerHttpRequest): 当Cookie存在时应返回其值")
    void testGetCookieValue_fromServerHttpRequest_cookieExists() {
        // given
        HttpCookie cookie = new HttpCookie(COOKIE_NAME, COOKIE_VALUE);
        MultiValueMap<String, HttpCookie> cookies = new org.springframework.util.LinkedMultiValueMap<>();
        cookies.add(COOKIE_NAME, cookie);
        when(serverHttpRequest.getCookies()).thenReturn(cookies);
        // when
        String value = ReactiveCookieUtils.getCookieValue(serverHttpRequest, COOKIE_NAME);
        // then
        assertEquals(COOKIE_VALUE, value);
    }

    @Test
    @DisplayName("getCookieValue(ServerHttpRequest): 当存在同名多个Cookie时应返回第一个的值")
    void testGetCookieValue_fromServerHttpRequest_multipleCookiesExist() {
        // given
        HttpCookie cookie1 = new HttpCookie(COOKIE_NAME, "value1");
        HttpCookie cookie2 = new HttpCookie(COOKIE_NAME, "value2");
        MultiValueMap<String, HttpCookie> cookies = new org.springframework.util.LinkedMultiValueMap<>();
        cookies.addAll(COOKIE_NAME, List.of(cookie1, cookie2));
        when(serverHttpRequest.getCookies()).thenReturn(cookies);
        // when
        String value = ReactiveCookieUtils.getCookieValue(serverHttpRequest, COOKIE_NAME);
        // then
        assertEquals("value1", value);
    }

    @Test
    @DisplayName("getCookieValue(ServerHttpRequest): 当Cookie不存在时应返回null")
    void testGetCookieValue_fromServerHttpRequest_cookieNotExists() {
        // given
        when(serverHttpRequest.getCookies()).thenReturn(new org.springframework.util.LinkedMultiValueMap<>());
        // when
        String value = ReactiveCookieUtils.getCookieValue(serverHttpRequest, COOKIE_NAME);
        // then
        assertNull(value);
    }

    @Test
    @DisplayName("getCookieValue(ServerRequest): 当Cookie存在时应返回其值")
    void testGetCookieValue_fromServerRequest_cookieExists() {
        // given
        HttpCookie cookie = new HttpCookie(COOKIE_NAME, COOKIE_VALUE);
        MultiValueMap<String, HttpCookie> cookies = new org.springframework.util.LinkedMultiValueMap<>();
        cookies.add(COOKIE_NAME, cookie);
        when(serverRequest.cookies()).thenReturn(cookies);
        // when
        String value = ReactiveCookieUtils.getCookieValue(serverRequest, COOKIE_NAME);
        // then
        assertEquals(COOKIE_VALUE, value);
    }

    /*
     * ===================================================================
     * deleteCookie Tests (ServerHttpResponse)
     * ===================================================================
     */
    @Test
    @DisplayName("deleteCookie(ServerHttpResponse): 使用默认路径删除Cookie")
    void testDeleteCookie_forHttpResponse_withDefaultPath() {
        // when
        ReactiveCookieUtils.deleteCookie(serverHttpResponse, COOKIE_NAME);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(serverHttpResponse).addCookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals("", capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(Duration.ZERO, capturedCookie.getMaxAge());
    }

    @Test
    @DisplayName("deleteCookie(ServerHttpResponse): 使用指定路径删除Cookie")
    void testDeleteCookie_forHttpResponse_withCustomPath() {
        // when
        ReactiveCookieUtils.deleteCookie(serverHttpResponse, COOKIE_NAME, CUSTOM_PATH);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(serverHttpResponse).addCookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(CUSTOM_PATH, capturedCookie.getPath());
        assertEquals(Duration.ZERO, capturedCookie.getMaxAge());
    }

    /*
     * ===================================================================
     * setCookie Tests (ServerHttpResponse)
     * ===================================================================
     */
    @Test
    @DisplayName("setCookie(ServerHttpResponse): 设置预构建的Cookie")
    void testSetCookie_forHttpResponse_withPrebuiltCookie() {
        // given
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, COOKIE_VALUE).build();
        // when
        ReactiveCookieUtils.setCookie(serverHttpResponse, cookie);
        // then
        verify(serverHttpResponse).addCookie(cookie);
    }

    @Test
    @DisplayName("setCookie(ServerHttpResponse): 使用默认值设置Cookie (带maxAge)")
    void testSetCookie_forHttpResponse_withDefaultsAndMaxAge() {
        // given
        int maxAge = 3600;
        // when
        ReactiveCookieUtils.setCookie(serverHttpResponse, COOKIE_NAME, COOKIE_VALUE, maxAge);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(serverHttpResponse).addCookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(COOKIE_VALUE, capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(Duration.ofSeconds(maxAge), capturedCookie.getMaxAge());
    }

    @Test
    @DisplayName("setCookie(ServerHttpResponse): 使用默认值设置会话Cookie (maxAge为null)")
    void testSetCookie_forHttpResponse_withDefaultsAndNullMaxAge() {
        // when
        ReactiveCookieUtils.setCookie(serverHttpResponse, COOKIE_NAME, COOKIE_VALUE, null);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(serverHttpResponse).addCookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(COOKIE_VALUE, capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());

        // 会话Cookie的maxAge在Spring中默认为-1秒，而不是null
        assertEquals(Duration.ofSeconds(-1), capturedCookie.getMaxAge());
    }

    @Test
    @DisplayName("setCookie(ServerHttpResponse): 设置完全自定义的Cookie")
    void testSetCookie_forHttpResponse_fullyCustomized() {
        // given
        int maxAge = 7200;
        // when
        ReactiveCookieUtils.setCookie(serverHttpResponse, COOKIE_NAME, COOKIE_VALUE, maxAge, false, CUSTOM_PATH);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(serverHttpResponse).addCookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(COOKIE_VALUE, capturedCookie.getValue());
        assertEquals(CUSTOM_PATH, capturedCookie.getPath());
        assertFalse(capturedCookie.isHttpOnly());
        assertEquals(Duration.ofSeconds(maxAge), capturedCookie.getMaxAge());
    }

    /*
     * ===================================================================
     * deleteCookie Tests (BodyBuilder)
     * ===================================================================
     */
    @Test
    @DisplayName("deleteCookie(BodyBuilder): 使用默认路径删除Cookie")
    void testDeleteCookie_forBodyBuilder_withDefaultPath() {
        // when
        ReactiveCookieUtils.deleteCookie(bodyBuilder, COOKIE_NAME);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(bodyBuilder).cookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(Duration.ZERO, capturedCookie.getMaxAge());
        assertEquals("/", capturedCookie.getPath());
    }

    @Test
    @DisplayName("deleteCookie(BodyBuilder): 使用指定路径删除Cookie")
    void testDeleteCookie_forBodyBuilder_withCustomPath() {
        // when
        ReactiveCookieUtils.deleteCookie(bodyBuilder, COOKIE_NAME, CUSTOM_PATH);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(bodyBuilder).cookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(CUSTOM_PATH, capturedCookie.getPath());
    }

    /*
     * ===================================================================
     * setCookie Tests (BodyBuilder)
     * ===================================================================
     */
    @Test
    @DisplayName("setCookie(BodyBuilder): 使用默认值设置Cookie")
    void testSetCookie_forBodyBuilder_withDefaults() {
        // given
        int maxAge = 3600;
        // when
        ReactiveCookieUtils.setCookie(bodyBuilder, COOKIE_NAME, COOKIE_VALUE, maxAge, false);
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(bodyBuilder).cookie(cookieCaptor.capture());

        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(COOKIE_VALUE, capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(Duration.ofSeconds(maxAge), capturedCookie.getMaxAge());
    }

    @Test
    @DisplayName("setCookie(BodyBuilder): 使用完全自定义参数设置Cookie")
    void testSetCookie_forBodyBuilder_fullyCustomized() {
        // given
        int maxAge = 7200;
        // when
        ReactiveCookieUtils.setCookie(bodyBuilder, COOKIE_NAME, COOKIE_VALUE, maxAge, false, false, CUSTOM_PATH, "Lax");
        // then
        ArgumentCaptor<ResponseCookie> cookieCaptor = ArgumentCaptor.forClass(ResponseCookie.class);
        verify(bodyBuilder).cookie(cookieCaptor.capture());
        ResponseCookie capturedCookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, capturedCookie.getName());
        assertEquals(COOKIE_VALUE, capturedCookie.getValue());
        assertEquals(CUSTOM_PATH, capturedCookie.getPath());
        assertFalse(capturedCookie.isHttpOnly());
        assertEquals(Duration.ofSeconds(maxAge), capturedCookie.getMaxAge());
    }

}
