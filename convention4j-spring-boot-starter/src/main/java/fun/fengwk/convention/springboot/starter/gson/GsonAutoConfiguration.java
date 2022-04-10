package fun.fengwk.convention.springboot.starter.gson;

import com.google.gson.Gson;
import fun.fengwk.convention.api.gson.GlobalGson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@ConditionalOnClass(Gson.class)
@Configuration
public class GsonAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GsonAutoConfiguration.class);

    @Bean
    public Gson gson() {
        Gson gson = GlobalGson.getInstance();
        LOG.info("{} autoconfiguration successfully", GsonAutoConfiguration.class.getSimpleName());
        return gson;
    }

}
