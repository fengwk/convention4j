#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql;

import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@BaseMapperScan
@ComponentScan
@Configuration
public class RepoMySqlAutoConfiguration {
}
