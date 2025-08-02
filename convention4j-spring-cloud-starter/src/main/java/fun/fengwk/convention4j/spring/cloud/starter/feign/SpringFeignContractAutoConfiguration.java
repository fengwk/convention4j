package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.Contract;
import feign.RequestInterceptor;
import feign.spring.SpringContract;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;

/**
 * Spring Cloud Feign 使用 {@link org.springframework.cloud.openfeign.support.SpringMvcContract} 部分注解未正确处理
 * 引入 Spring Feign 的 {@link SpringContract} 进行支持
 *
 * @author fengwk
 * @see <a href="https://github.com/OpenFeign/feign/issues/1383">Allow optional request body #1383</a>
 * @see <a href="https://github.com/spring-cloud/spring-cloud-openfeign/pull/263">Adds support for optional request body Fixes gh-126 #263</a>
 */
@ConditionalOnClass({RequestInterceptor.class, SpringContract.class})
@AutoConfiguration(beforeName = "org.springframework.cloud.openfeign.FeignClientsConfiguration")
public class SpringFeignContractAutoConfiguration {

    /**
     * @see FeignClientsConfiguration#feignContract(ConversionService)
     */
    @ConditionalOnMissingBean
    @Bean
    public Contract feignContract() {
        return new SpringContract();
    }

}
