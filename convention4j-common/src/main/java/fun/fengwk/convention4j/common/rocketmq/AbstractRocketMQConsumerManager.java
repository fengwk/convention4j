package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractRocketMQConsumerManager implements AutoCloseable {

    public void registerIfNecessary(Object bean) throws ClientException {
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : allDeclaredMethods) {
            RocketMQMessageListener listenerAnnotation = AnnotationUtils.findAnnotation(method, RocketMQMessageListener.class);
            if (listenerAnnotation != null) {
                register(bean, method, listenerAnnotation);
            }
        }
    }

    /**
     * 注册实现
     */
    protected abstract void register(Object bean, Method method, RocketMQMessageListener listenerAnnotation) throws ClientException;

}
