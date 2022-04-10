package fun.fengwk.convention.springboot.starter.rest;

import com.google.gson.Gson;
import fun.fengwk.convention.api.gson.GlobalGson;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fengwk
 */
@ComponentScan
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)// 预先初始化ErrorController
@Configuration
public class RestAutoConfiguration implements WebMvcConfigurer {

    /**
     * 使用GSON进行消息解析，配合{@link org.springframework.boot.autoconfigure.http.GsonHttpMessageConvertersConfiguration}。
     *
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public Gson gson() {
        return GlobalGson.getInstance();
    }

}
