package fun.fengwk.convention4j.common.rocketmq.inmemory;

import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class InMemoryRocketMQQueue {

    @Getter
    private final String topic;
    @Getter
    private final String consumerGroup;
    private final LinkedBlockingQueue<InMemoryRocketMQMessage> queue = new LinkedBlockingQueue<>();

    public InMemoryRocketMQQueue(String topic, String consumerGroup) {
        this.topic = topic;
        this.consumerGroup = consumerGroup;
    }

    public void enqueue(InMemoryRocketMQMessage message) throws InterruptedException {
        queue.put(message);
    }

    public InMemoryRocketMQMessage dequeue() throws InterruptedException {
        return queue.take();
    }

}
