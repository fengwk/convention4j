package fun.fengwk.convention4j.oauth2.infra;

import fun.fengwk.convention4j.oauth2.infra.repo.MysqlOAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.infra.repo.RedisOAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.server.OAuth2ServerAutoConfiguration;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapperScan;
import fun.fengwk.convention4j.springboot.test.starter.redis.EnableTestRedisServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@Import({ RedisOAuth2TokenRepository.class, MysqlOAuth2TokenRepository.class })
@BaseMapperScan
@EnableTestRedisServer
@SpringBootApplication(exclude = OAuth2ServerAutoConfiguration.class)
public class OAuth2InfraTestApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(OAuth2InfraTestApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

}