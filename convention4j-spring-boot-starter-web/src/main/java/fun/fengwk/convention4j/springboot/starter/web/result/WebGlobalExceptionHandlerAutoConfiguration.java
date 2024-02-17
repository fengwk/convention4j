package fun.fengwk.convention4j.springboot.starter.web.result;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@Import(WebGlobalExceptionHandler.class)
@AutoConfiguration
public class WebGlobalExceptionHandlerAutoConfiguration {
}
