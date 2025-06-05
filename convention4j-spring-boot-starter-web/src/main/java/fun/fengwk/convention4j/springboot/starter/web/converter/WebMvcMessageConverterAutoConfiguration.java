package fun.fengwk.convention4j.springboot.starter.web.converter;

import fun.fengwk.convention4j.common.json.gson.GsonHolder;
import fun.fengwk.convention4j.common.json.jackson.ObjectMapperHolder;
import fun.fengwk.convention4j.springboot.starter.json.GsonAutoConfiguration;
import fun.fengwk.convention4j.springboot.starter.json.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author fengwk
 */
@AutoConfiguration(after = { GsonAutoConfiguration.class, JacksonAutoConfiguration.class })
public class WebMvcMessageConverterAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setObjectMapper(ObjectMapperHolder.getInstance());
            } else if (converter instanceof GsonHttpMessageConverter gsonConverter) {
                gsonConverter.setGson(GsonHolder.getInstance());
            }
        }
    }

}
