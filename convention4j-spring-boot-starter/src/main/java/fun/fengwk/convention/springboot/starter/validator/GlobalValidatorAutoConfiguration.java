package fun.fengwk.convention.springboot.starter.validator;

import fun.fengwk.autovalidation.validator.GlobalValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

/**
 * 
 * @author fengwk
 */
@ConditionalOnBean(Validator.class)
@ConditionalOnClass({ Validator.class, GlobalValidator.class })
@AutoConfigureAfter(ValidationAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class GlobalValidatorAutoConfiguration implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalValidatorAutoConfiguration.class);

    private final Validator validator;
    
    public GlobalValidatorAutoConfiguration(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        GlobalValidator.setInstance(validator);
        LOG.info("{} setInstance {}", GlobalValidator.class.getSimpleName(), validator.getClass().getSimpleName());
    }

}
