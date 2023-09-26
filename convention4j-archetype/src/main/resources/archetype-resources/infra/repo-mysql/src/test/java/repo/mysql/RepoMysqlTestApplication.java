#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql;

import fun.fengwk.convention4j.springboot.test.starter.snowflake.EnableTestSnowflakeId;
import ${package}.core.CoreAutoConfiguration;
import ${package}.core.domain.model.FooFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@Import(FooFactory.class)
@EnableTestSnowflakeId
@SpringBootApplication(exclude = CoreAutoConfiguration.class)
public class RepoMysqlTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepoMysqlTestApplication.class, args);
    }

}
