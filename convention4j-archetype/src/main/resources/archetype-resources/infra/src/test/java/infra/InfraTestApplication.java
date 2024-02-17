#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infra;

import fun.fengwk.convention4j.springboot.test.starter.redis.EnableTestRedisServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ${package}.core.CoreAutoConfiguration;

/**
 * @author fengwk
 */
@EnableTestRedisServer
@SpringBootApplication(exclude = CoreAutoConfiguration.class)
public class InfraTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfraTestApplication.class, args);
    }

}
