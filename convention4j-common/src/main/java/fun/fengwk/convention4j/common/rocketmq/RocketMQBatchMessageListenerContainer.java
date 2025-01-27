package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMQBatchMessageListenerContainer implements AutoCloseable {

    private static int QUEUE_SCALE = 10;

    private static final List<BatchMessageListenerProcessor> PROCESSORS = LazyServiceLoader
        .loadServiceIgnoreLoadFailed(BatchMessageListenerProcessor.class);

    private final BatchMessageListener batchMessageListener;
    private final RocketMQBatchMessageListenerConfig listenerConfig;
    private final LinkedBlockingQueue<BatchMessages> batchMessagesQueue;
    private volatile boolean running;
    private SimpleConsumer consumer;
    private ConsumerPoller poller;
    private List<ConsumerExecutor> executors;

    public RocketMQBatchMessageListenerContainer(BatchMessageListener batchMessageListener,
                                                 RocketMQBatchMessageListenerConfig listenerConfig) {
        if (listenerConfig.getConsumptionThreadCount() <= 0) {
            throw new IllegalArgumentException("consumptionThreadCount must be greater than 0");
        }
        this.batchMessageListener = Objects.requireNonNull(batchMessageListener);
        this.listenerConfig = Objects.requireNonNull(listenerConfig);
        this.batchMessagesQueue = new LinkedBlockingQueue<>(
            listenerConfig.getConsumptionThreadCount() * QUEUE_SCALE);
    }

    public synchronized void start(ClientConfiguration clientConfiguration) throws ClientException {
        if (running) {
            throw new IllegalStateException("container already started");
        }
        this.running = true;

        SimpleConsumerBuilder scb = new SimpleConsumerBuilder();
        scb.setConsumerGroup(listenerConfig.getConsumerGroup());
        FilterExpression filterExpression = new FilterExpression(
            listenerConfig.getFilterExpression(), listenerConfig.getFilterExpressionType());
        scb.addSubscription(new Subscription(listenerConfig.getTopic(), filterExpression));
        this.consumer = scb.build(clientConfiguration);

        ConsumerPoller poller = new ConsumerPoller();
        poller.start();
        this.poller = poller;

        Integer consumptionThreadCount = listenerConfig.getConsumptionThreadCount();
        List<ConsumerExecutor> executors = new ArrayList<>();
        for (int i = 0; i < consumptionThreadCount; i++) {
            ConsumerExecutor executor = new ConsumerExecutor();
            executor.start();
            executors.add(executor);
        }
        this.executors = executors;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!running) {
            return;
        }
        this.running = false;

        // 需要先停止consumer
        IOException suppressedEx = null;
        try {
            consumer.close();
        } catch (IOException ex) {
            suppressedEx = ex;
        }

        // 然后再停止poller和executors
        poller.close();
        for (ConsumerExecutor executor : executors) {
            executor.close();
        }

        if (suppressedEx != null) {
            throw suppressedEx;
        }
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

    static abstract class AbstractConsumerComponent implements Runnable, AutoCloseable {

        protected Thread thread = new Thread(this);
        protected final CountDownLatch cdl = new CountDownLatch(1);
        protected volatile int status; // 0-init,1-start,2-closed

        public synchronized void start() {
            if (status == 1) {
                return;
            }
            if (status >= 2) {
                throw new IllegalStateException("the current runner has been closed");
            }
            this.status = 1;
            thread.start();
        }

        public synchronized void close() {
            if (status != 1) {
                return;
            }
            this.status = 2;
            thread.interrupt();
            try {
                cdl.await();
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

    }

    class ConsumerPoller extends AbstractConsumerComponent {

        @Override
        public void run() {
            try {
                while (running) {
                    try {
                        List<MessageView> messageViewList = consumer.receive(
                            listenerConfig.getMaxMessageNum(), Duration.ofMillis(listenerConfig.getInvisibleDurationMs()));
                        if (messageViewList.isEmpty()) {
                            Thread.yield();
                            continue;
                        }
                        BatchMessages batchMessages = new BatchMessages(messageViewList);
                        try {
                            batchMessagesQueue.put(batchMessages);
                        } catch (InterruptedException ignore) {
                            Thread.currentThread().interrupt();
                            Thread.yield();
                        }
                    } catch (Throwable err) {
                        if (err.getCause() instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                            Thread.yield();
                        } else {
                            log.error("receive message error", err);
                            Thread.yield();
                        }
                    }
                }
            } finally {
                cdl.countDown();
            }
        }

    }

    class ConsumerExecutor extends AbstractConsumerComponent {

        @Override
        public void run() {
            try {
                while (running) {
                    try {
                        BatchMessages batchMessages;
                        try {
                            batchMessages = batchMessagesQueue.take();
                        } catch (InterruptedException ignore) {
                            Thread.currentThread().interrupt();
                            Thread.yield();
                            continue;
                        }

                        List<MessageView> messageViewList = batchMessages.getMessageViewList();
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
                            log.error("batch consume error, messageViewList: {}", messageViewList, err);
                        }
                    } catch (Throwable err) {
                        if (err.getCause() instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        } else {
                            log.error("receive message error", err);
                        }
                        Thread.yield();
                    }
                }
            } finally {
                cdl.countDown();
            }
        }

    }

    @Data
    static class BatchMessages {
        private final List<MessageView> messageViewList;
    }

}
