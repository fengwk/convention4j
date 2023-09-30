package fun.fengwk.convention4j.springboot.starter.validation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author fengwk
 */
@Configuration
public class SpringConventionCheckerAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext applicationContext;

    public SpringConventionCheckerAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringConventionCheckerProvider.setBeanFactory(applicationContext);
    }

}
