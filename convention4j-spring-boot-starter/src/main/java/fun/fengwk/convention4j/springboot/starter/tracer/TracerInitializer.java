package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fengwk
 */
public class TracerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TracerUtils.initializeGlobalTracer(new Slf4jSpanFinisher());
        SpringBootSpanInitializer.setEnvironment(applicationContext.getEnvironment());
    }

}
