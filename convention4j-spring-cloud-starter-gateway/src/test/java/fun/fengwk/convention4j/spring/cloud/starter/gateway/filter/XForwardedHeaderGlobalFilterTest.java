package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.common.web.XForwardedHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author fengwk
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class XForwardedHeaderGlobalFilterTest {

    private static final String CLIENT_IP = "123.123.123.123";
    private static final String APP_NAME = "my-test-gateway";

    @Mock
    private XForwardedHeaderProperties properties;
    @Mock
    private Environment environment;
    @Mock
    private GatewayFilterChain filterChain;
    private XForwardedHeaderGlobalFilter filter;

    @BeforeEach
    void setUp() {
        // 默认情况下，所有功能都启用
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isXForwardedEnabled()).thenReturn(true);
        when(properties.isForwardedEnabled()).thenReturn(true);
        when(properties.isViaEnabled()).thenReturn(true);
        when(properties.isOriginalUriEnabled()).thenReturn(true);
        // 模拟 GatewayFilterChain 的行为
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    private ServerWebExchange createExchange(MockServerHttpRequest.BaseBuilder<?> requestBuilder) {
        return MockServerWebExchange.from(requestBuilder.build());
    }

    private MockServerHttpRequest.BaseBuilder<?> createBaseRequest() {
        return MockServerHttpRequest
            .get("https://example.com:8443/test/path?query=1")
            .remoteAddress(new InetSocketAddress(CLIENT_IP, 12345))
            .header(HttpHeaders.HOST, "example.com");
    }

    private HttpHeaders filterAndGetHeaders(ServerWebExchange exchange) {
        // 创建并执行过滤器
        filter = new XForwardedHeaderGlobalFilter(properties, environment);
        filter.filter(exchange, filterChain).block();
        // 验证 filterChain.filter 被调用，并捕获传入的 exchange
        var exchangeCaptor = org.mockito.ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(exchangeCaptor.capture());
        // 返回修改后的请求头
        return exchangeCaptor.getValue().getRequest().getHeaders();
    }

    @Test
    @DisplayName("检查过滤器顺序是否为最高优先级")
    void shouldReturnHighestPrecedenceOrder() {
        filter = new XForwardedHeaderGlobalFilter(properties, environment);
        assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }

    @Test
    @DisplayName("当过滤器被禁用时，不修改任何请求头")
    void whenFilterDisabled_shouldNotModifyRequest() {
        // GIVEN: 过滤器被禁用
        when(properties.isEnabled()).thenReturn(false);
        ServerWebExchange exchange = createExchange(createBaseRequest());
        ServerHttpRequest originalRequest = exchange.getRequest();
        // WHEN: 执行过滤器
        filter = new XForwardedHeaderGlobalFilter(properties, environment);
        StepVerifier.create(filter.filter(exchange, filterChain)).verifyComplete();
        // THEN: 请求未被修改，直接传递给 filterChain
        var exchangeCaptor = org.mockito.ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(exchangeCaptor.capture());
        assertThat(exchangeCaptor.getValue().getRequest()).isSameAs(originalRequest);
    }

    @Test
    @DisplayName("当所有功能启用且无已有头部时，正确添加所有头部")
    void whenAllEnabledAndNoExistingHeaders_shouldAddAllHeaders() {
        // GIVEN: 从环境中获取应用名称
        when(environment.getProperty("spring.application.name", "spring-cloud-gateway")).thenReturn(APP_NAME);
        ServerWebExchange exchange = createExchange(createBaseRequest());
        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: 验证所有头部都被正确添加
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_FOR.getName())).isEqualTo(CLIENT_IP);
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_PROTO.getName())).isEqualTo("https");
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_HOST.getName())).isEqualTo("example.com");
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_PORT.getName())).isEqualTo("8443");
        assertThat(headers.getFirst(XForwardedHeader.X_ORIGINAL_URI.getName())).isEqualTo("/test/path?query=1");
        assertThat(headers.getFirst(XForwardedHeader.FORWARDED.getName()))
            .isEqualTo("for=123.123.123.123;proto=https;host=example.com");
        assertThat(headers.getFirst(XForwardedHeader.VIA.getName())).isEqualTo(APP_NAME);
    }

    @Test
    @DisplayName("当已有相关头部时，正确追加新值")
    void whenExistingHeadersPresent_shouldAppendValues() {
        // GIVEN: 请求中已存在部分头部
        when(environment.getProperty("spring.application.name", "spring-cloud-gateway")).thenReturn(APP_NAME);
        MockServerHttpRequest.BaseBuilder<?> requestBuilder = createBaseRequest()
            .header(XForwardedHeader.X_FORWARDED_FOR.getName(), "203.0.113.195")
            .header(XForwardedHeader.FORWARDED.getName(), "for=203.0.113.195")
            .header(XForwardedHeader.VIA.getName(), "1.1 some-proxy");
        ServerWebExchange exchange = createExchange(requestBuilder);
        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: 验证新值被正确追加
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_FOR.getName())).isEqualTo("203.0.113.195, " + CLIENT_IP);
        assertThat(headers.getFirst(XForwardedHeader.FORWARDED.getName()))
            .isEqualTo("for=203.0.113.195, for=123.123.123.123;proto=https;host=example.com");
        assertThat(headers.getFirst(XForwardedHeader.VIA.getName())).isEqualTo("1.1 some-proxy, " + APP_NAME);
    }

    @Test
    @DisplayName("当spring.application.name未设置时，Via头部使用默认值")
    void whenAppNameNotSet_shouldUseDefaultViaIdentifier() {
        // GIVEN: 环境中没有 spring.application.name 属性
        when(environment.getProperty("spring.application.name", "spring-cloud-gateway")).thenReturn("spring-cloud-gateway");
        ServerWebExchange exchange = createExchange(createBaseRequest());
        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: Via 头部使用默认值
        assertThat(headers.getFirst(XForwardedHeader.VIA.getName())).isEqualTo("spring-cloud-gateway");
    }

    @Test
    @DisplayName("当XForwarded被禁用时，不添加X-Forwarded-*头部")
    void whenXForwardedDisabled_shouldNotAddXForwardedHeaders() {
        // GIVEN: XForwarded 功能被禁用
        when(properties.isXForwardedEnabled()).thenReturn(false);
        when(environment.getProperty(any(), anyString())).thenReturn(APP_NAME);
        ServerWebExchange exchange = createExchange(createBaseRequest());
        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: X-Forwarded-* 头部不存在
        assertThat(headers).doesNotContainKey(XForwardedHeader.X_FORWARDED_FOR.getName());
        assertThat(headers).doesNotContainKey(XForwardedHeader.X_FORWARDED_PROTO.getName());
        assertThat(headers).doesNotContainKey(XForwardedHeader.X_FORWARDED_HOST.getName());
        assertThat(headers).doesNotContainKey(XForwardedHeader.X_FORWARDED_PORT.getName());
        // 确保其他头部仍然被添加
        assertThat(headers).containsKey(XForwardedHeader.VIA.getName());
    }

    @Test
    @DisplayName("当请求为HTTP且无端口时，端口应为80")
    void whenHttpAndNoPort_shouldSetPortTo80() throws URISyntaxException {
        // GIVEN: 一个没有指定端口的HTTP请求
        when(environment.getProperty(any(), anyString())).thenReturn(APP_NAME);
        MockServerHttpRequest request = MockServerHttpRequest
            .get("http://example.com/path")
            .remoteAddress(new InetSocketAddress(CLIENT_IP, 12345))
            .header(HttpHeaders.HOST, "example.com").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: X-Forwarded-Port 应为 "80"
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_PORT.getName())).isEqualTo("80");
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_PROTO.getName())).isEqualTo("http");
    }

    @Test
    @DisplayName("当无法获取远程IP时，X-Forwarded-For应为'unknown'")
    void whenCannotGetRemoteIp_shouldUseUnknown() {
        // GIVEN: 一个无法获取远程地址的请求
        when(environment.getProperty(any(), anyString())).thenReturn(APP_NAME);
        MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com/").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        // WHEN: 执行过滤器
        HttpHeaders headers = filterAndGetHeaders(exchange);
        // THEN: X-Forwarded-For 的值为 "unknown"
        assertThat(headers.getFirst(XForwardedHeader.X_FORWARDED_FOR.getName())).isEqualTo("unknown");
        assertThat(headers.getFirst(XForwardedHeader.FORWARDED.getName())).startsWith("for=unknown;");
    }

}