package fun.fengwk.convention4j.springboot.starter.web.result;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author fengwk
 */
@Import({WebExceptionResultHandlerChain.class, ResultResponseBodyAdvice.class})
@AutoConfiguration
public class WebExceptionResultHandlerAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new NullableHandleThrowableMethodArgumentResolver());
    }

}
