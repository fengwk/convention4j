package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@ConditionalOnClass(RequestInterceptor.class)
@AutoConfiguration
public class FeignClientAutoConfiguration {

    @Bean
    public TracerFeignInterceptor tracerFeignInterceptor() {
        return new TracerFeignInterceptor();
    }

    @Bean
    public FeignInternalInvokerInterceptor feignInternalInvokerInterceptor() {
        return new FeignInternalInvokerInterceptor();
    }

}
