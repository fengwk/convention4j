package fun.fengwk.convention4j.springboot.starter.webflux.context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfiguration
public class WebFluxContextAutoConfiguration {

    @Bean
    public WebFluxContextFilter webFluxContextFilter() {
        return new WebFluxContextFilter();
    }

}
