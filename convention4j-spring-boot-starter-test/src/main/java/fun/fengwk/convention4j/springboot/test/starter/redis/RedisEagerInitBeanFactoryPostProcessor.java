package fun.fengwk.convention4j.springboot.test.starter.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * 由于对redisServer没有强依赖，因此一些需要使用redis的bean会更早初始化，例如revision，
 * 因此需要使用该BeanFactoryPostProcessor提前初始化RedisServer
 *
 * @author fengwk
 */
public class RedisEagerInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.getBean(RedisServerTestConfig.REDIS_SERVER_BEAN_NAME);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
