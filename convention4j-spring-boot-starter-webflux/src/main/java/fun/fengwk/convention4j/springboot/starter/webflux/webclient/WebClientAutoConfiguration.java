package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.common.json.jackson.ObjectMapperHolder;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
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
        // 配置HttpClient
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeout())
            .responseTimeout(webClientProperties.getResponseTimeout());
        // 构建WebClient
        return WebClient.builder()
            .codecs(this::configure)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter((request, next) -> WebFluxTracerContext.get()
                .flatMap(tc -> tc.execute(() -> {
                    // 开启WebClient的trace
                    SpanInfo spanInfo = SpanInfo.builder()
                        .operationName(request.method().name() + " " + request.url())
                        .kind(Tags.SPAN_KIND_CLIENT)
                        .build();
                    TraceInfo activateTi = tc.activate(spanInfo);
                    ClientRequest.Builder newRequestBuilder = ClientRequest.from(request);
                    // tracer信息注入
                    Tracer tracer = GlobalTracer.get();
                    Span activeSpan = tracer.activeSpan();
                    if (activeSpan != null) {
                        tracer.inject(activeSpan.context(), ClientRequestBuilderInject.FORMAT, newRequestBuilder);
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
                        return resp;
                    });
                })));
    }

    private void configure(CodecConfigurer configurer) {
        MimeType mimeType = MimeType.valueOf(MediaType.APPLICATION_JSON_VALUE);
        CodecConfigurer.CustomCodecs customCodecs = configurer.customCodecs();
        customCodecs.register(new Jackson2JsonDecoder(ObjectMapperHolder.getInstance(), mimeType));
        customCodecs.register(new Jackson2JsonEncoder(ObjectMapperHolder.getInstance(), mimeType));
    }

}
