package fun.fengwk.convention4j.common.rocketmq.inmemory;

import fun.fengwk.convention4j.common.lang.StringUtils;

import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.Message;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author fengwk
 */
public class InMemoryRocketMQConsumer implements Runnable, AutoCloseable {

    private final Thread thread = new Thread(this);
    private final InMemoryRocketMQBroker broker;
    private final String topic;
    private final String consumerGroup;
    private final MessageListener listener;
    private final FilterExpression filterExpression;

    public InMemoryRocketMQConsumer(InMemoryRocketMQBroker broker, String topic, String consumerGroup,
                                MessageListener listener, FilterExpression filterExpression) {
        this.broker = Objects.requireNonNull(broker);
        this.topic = Objects.requireNonNull(topic);
        this.consumerGroup = Objects.requireNonNull(consumerGroup);
        this.listener = Objects.requireNonNull(listener);
        this.filterExpression = Objects.requireNonNull(filterExpression);
        if (filterExpression.getFilterExpressionType() != FilterExpressionType.TAG) {
            throw new IllegalStateException("not support ' " + filterExpression.getFilterExpressionType() + " ' filter expression");
        }
    }

    public void start() {
        thread.start();
    }

    public boolean filterMessage(Message message) {
        switch (filterExpression.getFilterExpressionType()) {
            case TAG:
                return filterMessageWithTag(message, filterExpression.getExpression());
            default:
                throw new IllegalStateException("not support '" + filterExpression.getFilterExpressionType() + "' filter expression");
        }
    }

    private boolean filterMessageWithTag(Message message, String expression) {
        if (message == null || StringUtils.isBlank(expression)) {
            return false;
        }
        if (FilterExpression.SUB_ALL.getExpression().equals(expression.trim())) {
            return true;
        }
        String[] exprTagArr = expression.split("\\|\\|");
        Set<String> exprTagSet = new HashSet<>();
        for (String exprTag : exprTagArr) {
            exprTagSet.add(exprTag.trim());
        }
        return exprTagSet.contains(message.getTag().orElse(null));
    }

        @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                InMemoryRocketMQMessage testRocketMQMessage = broker.receiveMessage(topic, consumerGroup);
                if (filterMessage(testRocketMQMessage.getMessage())) {
                    listener.consume(new InMemoryRocketMQMessageView(testRocketMQMessage));
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() {
        thread.interrupt();
    }

}
