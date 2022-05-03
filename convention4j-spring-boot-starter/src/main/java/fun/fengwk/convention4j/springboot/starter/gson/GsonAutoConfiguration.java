package fun.fengwk.convention4j.springboot.starter.gson;

import com.google.gson.Gson;
import fun.fengwk.convention4j.common.gson.GlobalGson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@ConditionalOnClass(Gson.class)
@Configuration
public class GsonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GsonAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public Gson gson() {
        Gson gson = GlobalGson.getInstance();
        log.info("{} autoconfiguration successfully", Gson.class.getSimpleName());
        return gson;
    }

}
