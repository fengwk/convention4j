package fun.fengwk.convention4j.springboot.starter.webflux.reactiveclient;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * @author fengwk
 */
@Setter
public class ReactiveClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        ReactiveClientFactory factory = applicationContext.getBean(ReactiveClientFactory.class);
        return factory.create(type);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
