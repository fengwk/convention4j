#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app;

import fun.fengwk.convention4j.springboot.test.starter.redis.EnableTestRedisServer;
import fun.fengwk.convention4j.springboot.test.starter.snowflake.EnableTestSnowflakeId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@EnableTestRedisServer
@EnableTestSnowflakeId
@SpringBootApplication
public class AppTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppTestApplication.class, args);
    }

}
