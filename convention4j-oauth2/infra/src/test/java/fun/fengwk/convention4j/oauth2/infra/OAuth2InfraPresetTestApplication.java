package fun.fengwk.convention4j.oauth2.infra;

import fun.fengwk.convention4j.springboot.test.starter.redis.EnableTestRedisServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@EnableTestRedisServer
@SpringBootApplication
public class OAuth2InfraPresetTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2InfraPresetTestApplication.class, args);
    }

}