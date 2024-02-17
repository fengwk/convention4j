package fun.fengwk.convention4j.springboot.starter.json;

import com.google.gson.Gson;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.json.gson.GsonHolder;
import fun.fengwk.convention4j.common.json.gson.GsonJsonUtilsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@ConditionalOnClass(Gson.class)
@AutoConfiguration(before = { org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class, CodecsAutoConfiguration.class })
public class GsonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GsonAutoConfiguration.class);

    /**
     * @see org.springframework.boot.autoconfigure.http.GsonHttpMessageConvertersConfiguration
     */
    @ConditionalOnMissingBean
    @Bean
    public Gson gson() {
        if (!JsonUtils.registered()) {
            JsonUtils.register(GsonJsonUtilsAdapter.getInstance());
        }
        Gson gson = GsonHolder.getInstance();
        log.info("{} autoconfiguration successfully", Gson.class.getSimpleName());
        return gson;
    }

}
