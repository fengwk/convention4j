package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import fun.fengwk.convention4j.springboot.starter.transport.TransportHeaders;
import fun.fengwk.convention4j.springboot.starter.web.context.WebContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@ConditionalOnClass(RequestInterceptor.class)
@AutoConfiguration
public class FeignClientAutoConfiguration {

    @Bean
    public TracerFeignClientInterceptor tracerFeignClientInterceptor() {
        return new TracerFeignClientInterceptor();
    }

    @Bean
    public InternalInvokerFeignClinetInterceptor internalInvokerFeignClinetInterceptor() {
        return new InternalInvokerFeignClinetInterceptor();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(WebContext.class)
    static class TransportHeadersFeignClientConfiguration {

        @Bean
        public TransportHeadersFeignClientInterceptor transportHeadersFeignClientInterceptor(
            WebContext webContext, TransportHeaders transportHeaders) {
            return new TransportHeadersFeignClientInterceptor(webContext, transportHeaders);
        }

    }

}
