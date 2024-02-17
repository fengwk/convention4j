package fun.fengwk.convention4j.springboot.starter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.json.jackson.JacksonJsonUtilsAdapter;
import fun.fengwk.convention4j.common.json.jackson.ObjectMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@Slf4j
@ConditionalOnClass(ObjectMapper.class)
@AutoConfiguration(before = { GsonAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class, CodecsAutoConfiguration.class })
public class JacksonAutoConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        if (!JsonUtils.registered()) {
            JsonUtils.register(JacksonJsonUtilsAdapter.getInstance());
        }
        ObjectMapper instance = ObjectMapperHolder.getInstance();
        log.info("{} autoconfiguration successfully", ObjectMapper.class.getSimpleName());
        return instance;
    }

}
