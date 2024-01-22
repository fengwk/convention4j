package fun.fengwk.convention4j.oauth2.infra;

import fun.fengwk.convention4j.oauth2.core.OAuth2Properties;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@EnableConfigurationProperties(OAuth2Properties.class)
@BaseMapperScan
@ComponentScan
@Configuration
public class OAuth2InfraAutoConfiguration {
}
