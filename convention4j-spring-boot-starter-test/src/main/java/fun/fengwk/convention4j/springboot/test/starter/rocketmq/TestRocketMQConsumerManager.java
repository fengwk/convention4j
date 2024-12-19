package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fengwk
 */
@Slf4j
public class TestRocketMQConsumerManager extends AbstractRocketMQConsumerManager implements ApplicationListener<ContextRefreshedEvent> {

    private final TestRocketMQBroker broker;
    private final List<TestRocketMQConsumer> consumers = new CopyOnWriteArrayList<>();

    public TestRocketMQConsumerManager(TestRocketMQBroker broker) {
        this.broker = broker;
    }

    @Override
    protected void register(Object bean, Method method, RocketMQMessageListener listenerAnnotation) {
        String topic = listenerAnnotation.topic();
        String consumerGroup = listenerAnnotation.consumerGroup();
        FilterExpression filterExpression = new FilterExpression(
            listenerAnnotation.filterExpression(), listenerAnnotation.filterExpressionType());

        broker.registerQueueIfNecessary(topic, consumerGroup);

        RocketMQMessageListenerAdapter messageListener = new RocketMQMessageListenerAdapter(bean, method);
        TestRocketMQConsumer consumer = new TestRocketMQConsumer(broker, topic, consumerGroup, messageListener, filterExpression);
        consumers.add(consumer);
    }

    @Override
    protected void register(Object bean, Method method, RocketMQBatchMessageListener batchListenerAnnotation) {
        String topic = batchListenerAnnotation.topic();
        String consumerGroup = batchListenerAnnotation.consumerGroup();
        FilterExpression filterExpression = new FilterExpression(
            batchListenerAnnotation.filterExpression(), batchListenerAnnotation.filterExpressionType());

        broker.registerQueueIfNecessary(topic, consumerGroup);

        // 也使用单个消费的方式简单实现
        BatchMessageListenerAdapter batchListenerAdapter = new BatchMessageListenerAdapter(bean, method);
        TestRocketMQConsumer consumer = new TestRocketMQConsumer(broker, topic, consumerGroup,
            new BatchMessageListenerAdapterBridge(batchListenerAdapter), filterExpression);
        consumers.add(consumer);
    }

    @Override
    public void start() {
        // nothing to do
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("start all consumers");
        for (TestRocketMQConsumer consumer : consumers) {
            consumer.start();
        }
    }

    @Override
    public void close() {
        for (TestRocketMQConsumer consumer : consumers) {
            consumer.close();
        }
    }

}
