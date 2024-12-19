package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
public class PushConsumerRocketMQConsumerManager extends AbstractRocketMQConsumerManager {

    private final ClientConfiguration clientConfiguration;
    private final PushConsumerBuilderProcessor pushConsumerBuilderProcessor;

    private final ConcurrentMap<Method, PushConsumerBuilder> builderRegistry = new ConcurrentHashMap<>();
    private final ConcurrentMap<Method, PushConsumer> consumerRegistry = new ConcurrentHashMap<>();

    private final ConcurrentMap<Method, RocketMQBatchMessageListenerContainer> batchConsumerRegistry = new ConcurrentHashMap<>();

    public PushConsumerRocketMQConsumerManager(ClientConfiguration clientConfiguration,
                                               PushConsumerBuilderProcessor pushConsumerBuilderProcessor) {
        this.clientConfiguration = Objects.requireNonNull(clientConfiguration);
        this.pushConsumerBuilderProcessor = pushConsumerBuilderProcessor;
    }

    @Override
    protected void register(Object bean, Method method, RocketMQMessageListener listenerAnnotation) throws ClientException {
        PushConsumerBuilder pushConsumerBuilder = new PushConsumerBuilder();
        pushConsumerBuilder.setConsumerGroup(listenerAnnotation.consumerGroup());
        pushConsumerBuilder.setListener(new RocketMQMessageListenerAdapter(bean, method));
        pushConsumerBuilder.addSubscription(new Subscription(listenerAnnotation.topic(),
            new FilterExpression(listenerAnnotation.filterExpression(), listenerAnnotation.filterExpressionType())));
        if (pushConsumerBuilderProcessor != null) {
            pushConsumerBuilderProcessor.postProcess(pushConsumerBuilder);
        }
        builderRegistry.put(method, pushConsumerBuilder);
    }

    @Override
    protected void register(Object bean, Method method, RocketMQBatchMessageListener batchListenerAnnotation) throws ClientException {
        BatchMessageListenerAdapter batchListenerAdapter = new BatchMessageListenerAdapter(bean, method);
        RocketMQBatchMessageListenerContainer batchListenerContainer = new RocketMQBatchMessageListenerContainer(
            batchListenerAdapter, batchListenerAnnotation);
        batchConsumerRegistry.put(method, batchListenerContainer);
    }

    @Override
    public void start() throws ClientException {
        for (Map.Entry<Method, PushConsumerBuilder> entry : builderRegistry.entrySet()) {
            PushConsumer pushConsumer = entry.getValue().build(clientConfiguration);
            consumerRegistry.put(entry.getKey(), pushConsumer);
        }

        for (RocketMQBatchMessageListenerContainer container : batchConsumerRegistry.values()) {
            container.start(clientConfiguration);
        }
    }

    @Override
    public void close() {
        for (PushConsumer consumer : consumerRegistry.values()) {
            try {
                consumer.close();
            } catch (IOException ex) {
                log.error("close consumer error, consumer: {}", consumer, ex);
            }
        }

        for (RocketMQBatchMessageListenerContainer container : batchConsumerRegistry.values()) {
            try {
                container.close();
            } catch (IOException ex) {
                log.error("close batch container error, container: {}", container, ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
