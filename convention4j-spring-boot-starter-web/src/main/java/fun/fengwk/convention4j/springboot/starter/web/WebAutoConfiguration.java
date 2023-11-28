package fun.fengwk.convention4j.springboot.starter.web;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@ComponentScan
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)// 先初始化ErrorController，再配置ErrorMvcAutoConfiguration
@Configuration
public class WebAutoConfiguration {

}
