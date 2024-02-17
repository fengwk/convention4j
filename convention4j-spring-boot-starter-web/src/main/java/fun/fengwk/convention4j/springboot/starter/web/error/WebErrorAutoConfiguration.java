package fun.fengwk.convention4j.springboot.starter.web.error;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)// 先初始化ErrorController，再配置ErrorMvcAutoConfiguration
@Import(WebErrorController.class)
@AutoConfiguration
public class WebErrorAutoConfiguration {

}
