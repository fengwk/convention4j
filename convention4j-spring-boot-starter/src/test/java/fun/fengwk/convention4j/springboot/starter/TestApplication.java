package fun.fengwk.convention4j.springboot.starter;

import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapperScan;
import fun.fengwk.convention4j.springboot.test.starter.redis.EnableEmbeddedRedisServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@EnableEmbeddedRedisServer
@BaseMapperScan
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
