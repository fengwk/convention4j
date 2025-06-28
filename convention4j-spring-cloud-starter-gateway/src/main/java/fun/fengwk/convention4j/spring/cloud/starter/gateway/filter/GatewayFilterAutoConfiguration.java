package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

/**
 * @author fengwk
 */
@AutoConfiguration
public class GatewayFilterAutoConfiguration {

    @Bean
    public PreSetResponseHeaderGatewayFilterFactory preSetResponseHeaderGatewayFilterFactory() {
        return new PreSetResponseHeaderGatewayFilterFactory();
    }

    @Bean
    public ResourceGatewayFilterFactory resourceGatewayFilterFactory(ResourceLoader resourceLoader) {
        return new ResourceGatewayFilterFactory(resourceLoader);
    }

    @Bean
    public SetTracerHeadersGlobalFilter setTracerHeadersGlobalFilter() {
        return new SetTracerHeadersGlobalFilter();
    }

}
