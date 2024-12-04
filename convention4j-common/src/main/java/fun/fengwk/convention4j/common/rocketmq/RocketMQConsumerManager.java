package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMQConsumerManager implements AutoCloseable {

    private final ClientConfiguration clientConfiguration;
    private final PushConsumerBuilderProcessor pushConsumerBuilderProcessor;
    private final ConcurrentMap<Method, PushConsumer> registry = new ConcurrentHashMap<>();

    public RocketMQConsumerManager(ClientConfiguration clientConfiguration,
                                   PushConsumerBuilderProcessor pushConsumerBuilderProcessor) {
        this.clientConfiguration = Objects.requireNonNull(clientConfiguration);
        this.pushConsumerBuilderProcessor = pushConsumerBuilderProcessor;
    }

    public void registerIfNecessary(Object bean) throws ClientException {
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : allDeclaredMethods) {
            RocketMQMessageListener listenerAnnotation = AnnotationUtils.findAnnotation(method, RocketMQMessageListener.class);
            if (listenerAnnotation != null) {
                register(bean, method, listenerAnnotation);
            }
        }
    }

    private void register(Object bean, Method method, RocketMQMessageListener listenerAnnotation) throws ClientException {
        PushConsumerBuilder pushConsumerBuilder = new PushConsumerBuilder();
        pushConsumerBuilder.setConsumerGroup(listenerAnnotation.consumerGroup());
        pushConsumerBuilder.setListener(new RocketMQMessageListenerAdapter(bean, method));
        pushConsumerBuilder.addSubscription(new Subscription(listenerAnnotation.topic(),
            new FilterExpression(listenerAnnotation.filterExpression(), listenerAnnotation.filterExpressionType())));
        if (pushConsumerBuilderProcessor != null) {
            pushConsumerBuilderProcessor.postProcess(pushConsumerBuilder);
        }
        PushConsumer pushConsumer = pushConsumerBuilder.build(clientConfiguration);
        registry.put(method, pushConsumer);
    }

    @Override
    public void close() {
        for (PushConsumer consumer : registry.values()) {
            try {
                consumer.close();
            } catch (IOException ex) {
                log.error("close consumer error, consumer: {}", consumer, ex);
            }
        }
    }

}
