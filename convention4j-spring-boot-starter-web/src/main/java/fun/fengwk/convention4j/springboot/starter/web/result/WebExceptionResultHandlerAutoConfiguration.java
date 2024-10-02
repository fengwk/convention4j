package fun.fengwk.convention4j.springboot.starter.web.result;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@Import(WebExceptionResultHandlerChain.class)
@AutoConfiguration
public class WebExceptionResultHandlerAutoConfiguration {

}
