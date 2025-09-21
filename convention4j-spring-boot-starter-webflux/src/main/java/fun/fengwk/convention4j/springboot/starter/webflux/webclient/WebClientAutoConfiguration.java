package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.common.json.jackson.ObjectMapperHolder;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.transport.TransportHeaders;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import fun.fengwk.convention4j.springboot.starter.webflux.tracer.ClientRequestBuilderInject;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import io.netty.channel.ChannelOption;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
@Slf4j
@EnableConfigurationProperties(WebClientProperties.class)
@AutoConfiguration
public class WebClientAutoConfiguration {

    @Bean
    public TransportHeadersWebClientRequestModifier tracerXHeaderWebClientRequestModifier(
            TransportHeaders transportHeaders) {
        return new TransportHeadersWebClientRequestModifier(transportHeaders);
    }

    @Bean
    public InternalInvokerWebClientRequestModifier internalInvokerWebClientRequestModifier() {
        return new InternalInvokerWebClientRequestModifier();
    }

    /**
     * 配置自定义的序列化对象
     *
     * @see fun.fengwk.convention4j.springboot.starter.json.JacksonAutoConfiguration
     */
    @Bean
    public CodecCustomizer codecCustomizer() {
        return this::configure;
    }

    @RefreshScope
    @LoadBalanced
    @ConditionalOnMissingBean
    @Bean
    public WebClient.Builder webClientBuilder(WebClientProperties webClientProperties,
                                              ObjectProvider<List<WebClientRequestModifier>> requestModifiersProvider) {
        // @see InetAddressCachePolicy
        Long cacheTtl = parseLong(System.getProperty("sun.net.inetaddr.ttl"));
        Long negCacheTtl = parseLong(System.getProperty("sun.net.inetaddr.negative.ttl"));

        // 配置HttpClient
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeout())
                .resolver(spec -> {
                    if (cacheTtl != null) {
                        spec.cacheMaxTimeToLive(Duration.ofSeconds(cacheTtl));
                        spec.cacheMinTimeToLive(Duration.ofSeconds(cacheTtl));
                    }
                    if (negCacheTtl != null) {
                        spec.cacheNegativeTimeToLive(Duration.ofSeconds(negCacheTtl));
                    }
                })
                .responseTimeout(webClientProperties.getResponseTimeout());
        // 构建WebClient
        return WebClient.builder()
                .codecs(this::configure)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(tracerFilter())
                .filter(requestModifierFilter(requestModifiersProvider));
    }

    private ExchangeFilterFunction tracerFilter() {
        return (request, next) -> {
            Mono<ClientResponse> mono = ReactorTracerUtils.activeSpan()
                    .flatMap(spanOpt -> {
                        Span span = spanOpt.orElse(null);
                        ClientRequest.Builder newRequestBuilder = ClientRequest.from(request);
                        if (span != null) {
                            GlobalTracer.get().inject(span.context(), ClientRequestBuilderInject.FORMAT, newRequestBuilder);
                            span.setTag(Tags.HTTP_METHOD, request.method().name());
                        }
                        return next.exchange(newRequestBuilder.build());
                    })
                    .doOnNext(resp -> {
                        Span span = GlobalTracer.get().activeSpan();
                        if (span != null) {
                            HttpStatusCode httpStatusCode = resp.statusCode();
                            span.setTag(Tags.HTTP_STATUS, httpStatusCode.value());
                            if (httpStatusCode.is2xxSuccessful()) {
                                span.setTag(Tags.ERROR, false);
                            } else {
                                span.setTag(Tags.ERROR, true);
                            }
                        }
                    });

            SpanInfo spanInfo = SpanInfo.builder()
                    .operationName(request.url().toString())
                    .kind(Tags.SPAN_KIND_CLIENT)
                    .build();
            Tracer tracer = GlobalTracer.get();
            return ReactorTracerUtils.newSpan(tracer, spanInfo, mono);
        };
    }

    private ExchangeFilterFunction requestModifierFilter(
            ObjectProvider<List<WebClientRequestModifier>> requestModifiersProvider) {
        return (request, next) -> Mono.deferContextual(ctxView -> {
            WebFluxContext webFluxContext = WebFluxContext.get(ctxView);
            ClientRequest.Builder newRequestBuilder = ClientRequest.from(request);
            if (webFluxContext != null) {
                // 执行所有请求修改器
                List<WebClientRequestModifier> requestModifiers = requestModifiersProvider
                        .getIfAvailable(Collections::emptyList);
                for (WebClientRequestModifier requestModifier : requestModifiers) {
                    requestModifier.modify(webFluxContext, newRequestBuilder);
                }
            }
            return next.exchange(newRequestBuilder.build());
        });
    }

    private void configure(CodecConfigurer configurer) {
        MimeType mimeType = MimeType.valueOf(MediaType.APPLICATION_JSON_VALUE);
        CodecConfigurer.CustomCodecs customCodecs = configurer.customCodecs();
        customCodecs.register(new Jackson2JsonDecoder(ObjectMapperHolder.getInstance(), mimeType));
        customCodecs.register(new Jackson2JsonEncoder(ObjectMapperHolder.getInstance(), mimeType));
    }

    private Long parseLong(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (Exception ignore) {
            return null;
        }
    }

}
