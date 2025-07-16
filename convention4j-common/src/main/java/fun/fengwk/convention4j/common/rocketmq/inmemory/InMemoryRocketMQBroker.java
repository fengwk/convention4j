package fun.fengwk.convention4j.common.rocketmq.inmemory;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
public class InMemoryRocketMQBroker {

    // topic -> consumerGroup -> queue
    private final ConcurrentMap<String , Map<String, InMemoryRocketMQQueue>> registry = new ConcurrentHashMap<>();

    public boolean registerQueueIfNecessary(String topic, String consumerGroup) {
        Map<String, InMemoryRocketMQQueue> topicRegistry = registry.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        InMemoryRocketMQQueue queue = new InMemoryRocketMQQueue(topic, consumerGroup);
        InMemoryRocketMQQueue oldQueue = topicRegistry.putIfAbsent(consumerGroup, queue);
        return oldQueue == null;
    }

    public InMemoryRocketMQSendReceipt sendMessage(Message message) throws ClientException, InterruptedException {
        InMemoryRocketMQSendReceipt sendReceipt = new InMemoryRocketMQSendReceipt();

        Map<String, InMemoryRocketMQQueue> topicRegistry = registry.get(message.getTopic());
        if (topicRegistry == null) {
            throw new ClientException("topic not found: " + message.getTopic());
        }

        InMemoryRocketMQMessage testRocketMQMessage = new InMemoryRocketMQMessage(message, sendReceipt.getMessageId());
        for (InMemoryRocketMQQueue queue : topicRegistry.values()) {
            queue.enqueue(testRocketMQMessage);
        }
        return sendReceipt;
    }

    public InMemoryRocketMQMessage receiveMessage(String topic, String consumerGroup) throws InterruptedException {
        Map<String, InMemoryRocketMQQueue> topicRegistry = registry.get(topic);
        if (topicRegistry == null) {
            throw new IllegalStateException("topic not found: " + topic);
        }
        InMemoryRocketMQQueue queue = topicRegistry.get(consumerGroup);
        if (queue == null) {
            throw new IllegalStateException("consumerGroup not found: " + consumerGroup);
        }
        return queue.dequeue();
    }

}
