package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fengwk
 */
public class TracerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REACTOR_TRACER_UTILS_CLASS = "fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils";
    private static final String FLUX_CLASS = "reactor.core.publisher.Mono";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Slf4jSpanFinisher finisher = new Slf4jSpanFinisher();
        if (ClassUtils.testClassAvailable(REACTOR_TRACER_UTILS_CLASS) && ClassUtils.testClassAvailable(FLUX_CLASS)) {
            ReactorTracerUtils.initialize(finisher);
        } else {
            TracerUtils.initialize(finisher);
        }
        SpringBootSpanInitializer.setEnvironment(applicationContext.getEnvironment());
    }

}
