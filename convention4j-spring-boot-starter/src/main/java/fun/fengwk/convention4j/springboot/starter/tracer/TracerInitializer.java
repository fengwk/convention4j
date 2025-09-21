package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Tracer;
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
        if (isReactorTracerAvailable()) {
            ReactorTracerUtils.initializeGlobalTracer();
        } else {
            TracerUtils.initializeGlobalTracer();
        }
        SpringBootSpanInitializer.setEnvironment(applicationContext.getEnvironment());
    }

    static boolean isReactorTracerAvailable() {
        return ClassUtils.testClassAvailable(REACTOR_TRACER_UTILS_CLASS) && ClassUtils.testClassAvailable(FLUX_CLASS);
    }

}
