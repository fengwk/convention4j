package fun.fengwk.convention4j.springboot.starter.webflux.tracer;

import fun.fengwk.convention4j.springboot.starter.tracer.SpringBootSpanInitializer;
import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fengwk
 */
public class WebFluxTracerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ReactorTracerUtils.initialize(new Slf4jSpanFinisher());
        SpringBootSpanInitializer.setEnvironment(applicationContext.getEnvironment());
    }

}
