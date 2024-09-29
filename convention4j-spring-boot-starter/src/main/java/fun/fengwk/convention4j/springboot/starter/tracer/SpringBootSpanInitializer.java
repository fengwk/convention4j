package fun.fengwk.convention4j.springboot.starter.tracer;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.tracer.util.SpanInitializer;
import io.opentracing.Span;
import org.springframework.core.env.Environment;

/**
 * @author fengwk
 */
@AutoService(SpanInitializer.class)
public class SpringBootSpanInitializer implements SpanInitializer {

    public static final String APP = "app";

    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    private static volatile Environment ENVIRONMENT;

    static void setEnvironment(Environment environment) {
        SpringBootSpanInitializer.ENVIRONMENT = environment;
    }

    @Override
    public void initializeSpan(Span span) {
        if (ENVIRONMENT == null) {
            return;
        }
        String app = ENVIRONMENT.getProperty(SPRING_APPLICATION_NAME);
        if (StringUtils.isEmpty(app)) {
            app = "unknown";
        }
        span.setTag(APP, app);
    }

}
