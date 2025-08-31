package fun.fengwk.convention4j.common.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author fengwk
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CookieUtils 单元测试")
public class CookieUtilsTest {

    private static final String COOKIE_NAME = "test-cookie";
    private static final String COOKIE_VALUE = "test-value";
    private static final String CUSTOM_PATH = "/custom";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    /*
     * ===================================================================
     * getCookie / getCookies / getCookieValue Tests
     * ===================================================================
     */

    @Test
    @DisplayName("getCookie: 当Cookie存在时应返回第一个匹配的Cookie")
    void testGetCookie_whenCookieExists_shouldReturnFirstMatch() {
        // given
        Cookie cookie1 = new Cookie(COOKIE_NAME, "value1");
        Cookie cookie2 = new Cookie("other-cookie", "other-value");
        Cookie cookie3 = new Cookie(COOKIE_NAME, "value2");
        Cookie[] cookies = {cookie1, cookie2, cookie3};
        when(request.getCookies()).thenReturn(cookies);

        // when
        Cookie result = CookieUtils.getCookie(request, COOKIE_NAME);
        // then
        assertNotNull(result);
        assertEquals(COOKIE_NAME, result.getName());
        assertEquals("value1", result.getValue());
    }

    @Test
    @DisplayName("getCookie: 当Cookie不存在时应返回null")
    void testGetCookie_whenCookieNotExists_shouldReturnNull() {
        // given
        Cookie[] cookies = {new Cookie("other-cookie", "other-value")};
        when(request.getCookies()).thenReturn(cookies);
        // when
        Cookie result = CookieUtils.getCookie(request, COOKIE_NAME);
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("getCookie: 当请求中没有Cookie时应返回null")
    void testGetCookie_whenRequestHasNoCookies_shouldReturnNull() {
        // given
        when(request.getCookies()).thenReturn(null);
        // when
        Cookie result = CookieUtils.getCookie(request, COOKIE_NAME);
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("getCookies: 应返回所有同名的Cookie")
    void testGetCookies_shouldReturnAllMatchingCookies() {
        // given
        Cookie cookie1 = new Cookie(COOKIE_NAME, "value1");
        Cookie cookie2 = new Cookie("other-cookie", "other-value");
        Cookie cookie3 = new Cookie(COOKIE_NAME, "value2");
        Cookie[] cookies = {cookie1, cookie2, cookie3};
        // when
        List<Cookie> resultList = CookieUtils.getCookies(cookies, COOKIE_NAME);
        // then
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals("value1", resultList.get(0).getValue());
        assertEquals("value2", resultList.get(1).getValue());
    }

    @Test
    @DisplayName("getCookies: 当没有匹配的Cookie时应返回空列表")
    void testGetCookies_whenNoMatch_shouldReturnEmptyList() {
        // given
        Cookie[] cookies = {new Cookie("other-cookie", "other-value")};
        // when
        List<Cookie> resultList = CookieUtils.getCookies(cookies, COOKIE_NAME);
        // then
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
    }

    @Test
    @DisplayName("getCookieValue: 应返回第一个匹配的Cookie的值")
    void testGetCookieValue_shouldReturnValueOfFirstMatch() {
        // given
        Cookie cookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // when
        String value = CookieUtils.getCookieValue(request, COOKIE_NAME);
        // then
        assertEquals(COOKIE_VALUE, value);
    }

    @Test
    @DisplayName("getCookieValue: 当Cookie不存在时应返回null")
    void testGetCookieValue_whenCookieNotExists_shouldReturnNull() {
        // given
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // when
        String value = CookieUtils.getCookieValue(request, COOKIE_NAME);
        // then
        assertNull(value);
    }

    /*
     * ===================================================================
     * deleteCookie Tests
     * ===================================================================
     */

    @Test
    @DisplayName("deleteCookie: 使用默认路径删除Cookie")
    void testDeleteCookie_withDefaultPath() {
        // when
        CookieUtils.deleteCookie(response, COOKIE_NAME);
        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, captured.getName());
        assertEquals("", captured.getValue());
        assertEquals(0, captured.getMaxAge());
        assertEquals("/", captured.getPath());
    }

    @Test
    @DisplayName("deleteCookie: 使用指定路径删除Cookie")
    void testDeleteCookie_withCustomPath() {
        // when
        CookieUtils.deleteCookie(response, COOKIE_NAME, CUSTOM_PATH);
        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie captured = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, captured.getName());
        assertEquals(CUSTOM_PATH, captured.getPath());
        assertEquals(0, captured.getMaxAge());
    }

    /*
     * ===================================================================
     * setCookie Tests
     * ===================================================================
     */

    @Test
    @DisplayName("setCookie: 设置预构建的Cookie")
    void testSetCookie_withPrebuiltCookie() {
        // given
        Cookie cookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
        // when
        CookieUtils.setCookie(response, cookie);
        // then
        verify(response).addCookie(cookie);
    }

    @Test
    @DisplayName("setCookie: 使用便捷方法设置持久化Cookie")
    void testSetCookie_withConvenienceMethod() {
        // when
        CookieUtils.setCookie(response, COOKIE_NAME, COOKIE_VALUE, 3600, true);
        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie captured = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, captured.getName());
        assertEquals(COOKIE_VALUE, captured.getValue());
        assertEquals(3600, captured.getMaxAge());
        assertTrue(captured.isHttpOnly());
        assertTrue(captured.getSecure());
        assertEquals("/", captured.getPath());
    }

    @Test
    @DisplayName("setCookie: 设置完全自定义的Cookie")
    void testSetCookie_fullyCustomized() {
        // when
        CookieUtils.setCookie(response, COOKIE_NAME, COOKIE_VALUE, 7200, false, false, CUSTOM_PATH, null);
        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie captured = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, captured.getName());
        assertEquals(COOKIE_VALUE, captured.getValue());
        assertEquals(7200, captured.getMaxAge());
        assertFalse(captured.isHttpOnly());
        assertFalse(captured.getSecure());
        assertEquals(CUSTOM_PATH, captured.getPath());
    }

    @Test
    @DisplayName("setCookie: 设置会话Cookie (maxAge为null)")
    void testSetCookie_forSessionCookie() {
        // when
        CookieUtils.setCookie(response, COOKIE_NAME, COOKIE_VALUE, null, true, true, "/", null);
        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie captured = cookieCaptor.getValue();
        // 根据Servlet规范，未设置maxAge的Cookie，其getMaxAge()返回-1
        assertEquals(-1, captured.getMaxAge());
        assertEquals(COOKIE_NAME, captured.getName());
    }

}
