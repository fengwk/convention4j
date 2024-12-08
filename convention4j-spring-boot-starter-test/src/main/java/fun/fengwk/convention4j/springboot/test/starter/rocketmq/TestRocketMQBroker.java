package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Slf4j
public class TestRocketMQBroker {

    // topic -> consumerGroup -> queue
    private final ConcurrentMap<String , Map<String, TestRocketMQQueue>> registry = new ConcurrentHashMap<>();

    public void registerQueueIfNecessary(String topic, String consumerGroup) {
        Map<String, TestRocketMQQueue> topicRegistry = registry.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        topicRegistry.computeIfAbsent(consumerGroup, c -> new TestRocketMQQueue(topic, consumerGroup));
    }

    public TestRocketMQSendReceipt sendMessage(Message message) throws InterruptedException {
        TestRocketMQSendReceipt sendReceipt = new TestRocketMQSendReceipt();

        Map<String, TestRocketMQQueue> topicRegistry = registry.get(message.getTopic());
        if (topicRegistry == null) {
            log.warn("send message, but not found topic: {}", message.getTopic());
            return sendReceipt;
        }

        TestRocketMQMessage testRocketMQMessage = new TestRocketMQMessage(message, sendReceipt.getMessageId());
        for (TestRocketMQQueue queue : topicRegistry.values()) {
            queue.enqueue(testRocketMQMessage);
        }
        return sendReceipt;
    }

    public TestRocketMQMessage receiveMessage(String topic, String consumerGroup) throws InterruptedException {
        Map<String, TestRocketMQQueue> topicRegistry = registry.get(topic);
        if (topicRegistry == null) {
            throw new IllegalStateException("receive message, but not found topic: " + topic);
        }
        TestRocketMQQueue queue = topicRegistry.get(consumerGroup);
        if (queue == null) {
            throw new IllegalStateException("receive message, but not found consumerGroup: " + consumerGroup);
        }
        return queue.dequeue();
    }

}
