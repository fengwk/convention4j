package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfiguration
public class WebFluxTracerAutoConfiguration {

    @Bean
    public WebFluxTracerFilter webFluxTracerFilter() {
        return new WebFluxTracerFilter();
    }

}
