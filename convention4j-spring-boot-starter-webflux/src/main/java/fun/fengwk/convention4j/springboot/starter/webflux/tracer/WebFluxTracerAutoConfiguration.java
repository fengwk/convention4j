package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import fun.fengwk.convention4j.springboot.starter.webflux.webclient.XHeaderWebClientRequestModifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfiguration
public class WebFluxTracerAutoConfiguration {

    @Bean
    public XHeaderWebClientRequestModifier tracerXHeaderWebClientRequestModifier() {
        return new XHeaderWebClientRequestModifier();
    }

}
