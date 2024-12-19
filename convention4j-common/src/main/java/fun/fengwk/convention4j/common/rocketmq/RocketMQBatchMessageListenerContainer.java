package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMQBatchMessageListenerContainer implements Runnable, AutoCloseable {

    private static final List<BatchMessageListenerProcessor> PROCESSORS = LazyServiceLoader
        .loadServiceIgnoreLoadFailed(BatchMessageListenerProcessor.class);

    private final BatchMessageListener batchMessageListener;
    private final RocketMQBatchMessageListener listenerConfig;
    private SimpleConsumer consumer;
    private Thread thread;
    private CountDownLatch cdl;
    private volatile boolean running;

    public RocketMQBatchMessageListenerContainer(BatchMessageListener batchMessageListener,
                                                 RocketMQBatchMessageListener listenerConfig) {
        this.batchMessageListener = Objects.requireNonNull(batchMessageListener);
        this.listenerConfig = Objects.requireNonNull(listenerConfig);
    }

    public synchronized void start(ClientConfiguration clientConfiguration) throws ClientException {
        if (thread != null) {
            throw new IllegalStateException("container already started");
        }
        SimpleConsumerBuilder scb = new SimpleConsumerBuilder();
        scb.setConsumerGroup(listenerConfig.consumerGroup());
        FilterExpression filterExpression = new FilterExpression(
            listenerConfig.filterExpression(), listenerConfig.filterExpressionType());
        scb.addSubscription(new Subscription(listenerConfig.topic(), filterExpression));
        this.consumer = scb.build(clientConfiguration);
        this.thread = new Thread(this);
        this.cdl = new CountDownLatch(1);
        this.running = true;
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    List<MessageView> messageViewList = consumer.receive(
                        listenerConfig.maxMessageNum(), Duration.ofMillis(listenerConfig.invisibleDurationMs()));
                    if (messageViewList.isEmpty()) {
                        Thread.yield();
                        continue;
                    }
                    ProcessorContext context = new ProcessorContext();
                    preProcess(messageViewList, context);
                    try {
                        Collection<MessageView> acks = batchMessageListener.consume(messageViewList);
                        postProcess(messageViewList, context, acks);
                        for (MessageView ack : acks) {
                            try {
                                consumer.ack(ack);
                            } catch (ClientException ex) {
                                log.error("batch consumer ack error, ack: {}", ack, ex);
                            }
                        }
                    } catch (Throwable err) {
                        log.error("batch consume error, messageViewList: {}", messageViewList);
                    }
                } catch (Throwable err) {
                    log.error("receive message error", err);
                    Thread.yield();
                }
            }
        } finally {
            cdl.countDown();
        }
    }

    @Override
    public synchronized void close() throws IOException, InterruptedException {
        consumer.close();
        this.running = false;
        thread.interrupt();
        cdl.await();

        this.consumer = null;
        this.thread = null;
        this.cdl = null;
    }

    private void preProcess(List<MessageView> messageViewList, ProcessorContext context) {
        for (BatchMessageListenerProcessor processor : PROCESSORS) {
            try {
                processor.preProcess(messageViewList, context);
            } catch (Throwable err) {
                log.error("batch message pre process error", err);
            }
        }
    }

    private void postProcess(List<MessageView> messageViewList, ProcessorContext context,
                             Collection<MessageView> acks) {
        ListIterator<BatchMessageListenerProcessor> listIterator = PROCESSORS.listIterator(PROCESSORS.size());
        while (listIterator.hasPrevious()) {
            try {
                listIterator.previous().postProcess(messageViewList, context, acks);
            } catch (Throwable err) {
                log.error("batch message post process error", err);
            }
        }
    }

}
