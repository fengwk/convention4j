#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.cache.redis;

import fun.fengwk.convention4j.springboot.test.starter.redis.EnableTestRedisServer;
import ${package}.core.CoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@EnableTestRedisServer
@SpringBootApplication(exclude = CoreAutoConfiguration.class)
public class CacheRedisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheRedisTestApplication.class, args);
    }

}
