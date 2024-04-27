package fun.fengwk.convention4j.springboot.starter.xheader;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfiguration
public class XHeaderAutoConfiguration {

    @Bean
    public XHeaderRegistry xHeaderRegistry() {
        return new DefaultXHeaderRegistry();
    }

}
