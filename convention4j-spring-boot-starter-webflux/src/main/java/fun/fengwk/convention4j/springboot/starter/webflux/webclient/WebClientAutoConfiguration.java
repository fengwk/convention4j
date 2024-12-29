package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.common.json.jackson.ObjectMapperHolder;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.springboot.starter.webflux.context.TraceInfo;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext;
import fun.fengwk.convention4j.springboot.starter.webflux.tracer.ClientRequestBuilderInject;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.net.InetAddress;
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

    /**
     * 配置自定义的序列化对象
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
            .filter((request, next) -> WebFluxTracerContext.get()
                .flatMap(tc -> tc.execute(() -> {
                    // 开启WebClient的trace
                    SpanInfo spanInfo = SpanInfo.builder()
                        .operationName(request.url().toString())
                        .kind(Tags.SPAN_KIND_CLIENT)
                        .build();
                    TraceInfo activateTi = tc.activate(spanInfo);
                    // tracer信息注入
                    Tracer tracer = GlobalTracer.get();
                    Span activeSpan = tracer.activeSpan();
                    ClientRequest.Builder newRequestBuilder = ClientRequest.from(request);
                    if (activeSpan != null) {
                        tracer.inject(activeSpan.context(), ClientRequestBuilderInject.FORMAT, newRequestBuilder);
                        activeSpan.setTag(Tags.HTTP_METHOD, request.method().name());
                    }
                    WebFluxContext webFluxContext = tc.getWebFluxContext();
                    if (webFluxContext != null) {
                        // 执行所有请求修改器
                        List<WebClientRequestModifier> requestModifiers = requestModifiersProvider
                            .getIfAvailable(Collections::emptyList);
                        for (WebClientRequestModifier requestModifier : requestModifiers) {
                            requestModifier.modify(webFluxContext, newRequestBuilder);
                        }
                    }
                    // 继续执行
                    return next.exchange(newRequestBuilder.build()).map(resp -> {
                        // 关闭WebClient的trace
                        tc.finish(activateTi);
                        Span finishActiveSpan = GlobalTracer.get().activeSpan();
                        if (finishActiveSpan != null) {
                            HttpStatusCode httpStatusCode = resp.statusCode();
                            finishActiveSpan.setTag(Tags.HTTP_STATUS, httpStatusCode.value());
                            if (httpStatusCode.is2xxSuccessful()) {
                                finishActiveSpan.setTag(Tags.ERROR, false);
                            } else {
                                finishActiveSpan.setTag(Tags.ERROR, true);
                            }
                        }
                        return resp;
                    }).doOnError(err -> {
                        log.error("WebClient request error", err);
                        Span finishActiveSpan = GlobalTracer.get().activeSpan();
                        if (finishActiveSpan != null) {
                            finishActiveSpan.setTag(Tags.ERROR, true);
                        }
                        tc.finish(activateTi);
                    });
                })));
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
