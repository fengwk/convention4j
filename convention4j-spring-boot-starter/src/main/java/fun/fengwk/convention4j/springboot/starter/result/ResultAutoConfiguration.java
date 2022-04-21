package fun.fengwk.convention4j.springboot.starter.result;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@Import(ResultExceptionHandler.class)
@EnableAspectJAutoProxy
@Configuration
public class ResultAutoConfiguration {
}
