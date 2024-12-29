package fun.fengwk.convention4j.springboot.starter.xxljob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@Slf4j
@EnableConfigurationProperties(XxlJobProperties.class)
@AutoConfiguration
public class XxlJobAutoConfiguration {

    @Bean
    public RefreshableXxlJobSpringExecutor refreshableXxlJobSpringExecutor(@Value("${spring.application.name:unknown}") String appName) {
        return new RefreshableXxlJobSpringExecutor(appName);
    }

}
