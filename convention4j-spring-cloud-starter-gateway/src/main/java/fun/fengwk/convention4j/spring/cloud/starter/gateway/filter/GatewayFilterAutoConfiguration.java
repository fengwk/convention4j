package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * @author fengwk
 */
@EnableConfigurationProperties(XForwardHeaderProperties.class)
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

    @Bean
    public XForwardHeaderGlobalFilter xForwardHeaderGlobalFilter(XForwardHeaderProperties xForwardHeaderProperties,
                                                                 Environment environment) {
        return new XForwardHeaderGlobalFilter(xForwardHeaderProperties, environment);
    }

}
