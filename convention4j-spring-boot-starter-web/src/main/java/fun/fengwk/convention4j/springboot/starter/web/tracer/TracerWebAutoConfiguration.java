package fun.fengwk.convention4j.springboot.starter.web.tracer;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fengwk
 */
@AllArgsConstructor
@AutoConfiguration
public class TracerWebAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new TracerWebInterceptor());
        registration.order(Ordered.HIGHEST_PRECEDENCE);
    }

}
