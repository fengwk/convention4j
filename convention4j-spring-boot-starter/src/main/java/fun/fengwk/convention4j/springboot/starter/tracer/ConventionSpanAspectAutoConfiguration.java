package fun.fengwk.convention4j.springboot.starter.tracer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import reactor.core.publisher.Mono;

/**
 * @author fengwk
 */
@EnableAspectJAutoProxy
@AutoConfiguration
public class ConventionSpanAspectAutoConfiguration {

    @ConditionalOnMissingClass({"reactor.core.publisher.Mono"})
    @Configuration(proxyBeanMethods = false)
    static class DefaultConventionSpanAspectConfiguration {

        @Bean
        public ConventionSpanAspect spanAspect() {
            return new ConventionSpanAspect();
        }

    }

    @ConditionalOnClass(Mono.class)
    @Configuration(proxyBeanMethods = false)
    static class ReactorConventionSpanAspectConfiguration {

        @Bean
        public ConventionSpanAspect spanAspect() {
            return new ReactorConventionSpanAspect();
        }

    }

}
