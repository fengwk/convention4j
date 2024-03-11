package fun.fengwk.convention4j.springboot.starter.web.context;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AllArgsConstructor
@AutoConfiguration
public class WebContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebContext webContext() {
        return new TtlWebContext();
    }

}
