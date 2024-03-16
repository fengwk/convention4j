package fun.fengwk.convention4j.springboot.starter.tracer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author fengwk
 */
@EnableAspectJAutoProxy
@AutoConfiguration
public class ConventionSpanAspectAutoConfiguration {

    @Bean
    public ConventionSpanAspect spanAspect() {
        return new ConventionSpanAspect();
    }

}
