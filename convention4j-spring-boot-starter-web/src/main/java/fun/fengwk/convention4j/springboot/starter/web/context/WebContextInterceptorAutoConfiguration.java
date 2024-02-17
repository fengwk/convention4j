package fun.fengwk.convention4j.springboot.starter.web.context;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fengwk
 */
@AllArgsConstructor
@AutoConfiguration
public class WebContextInterceptorAutoConfiguration implements WebMvcConfigurer {

    private final WebContext webContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new WebContextInterceptor(webContext));
    }

}
