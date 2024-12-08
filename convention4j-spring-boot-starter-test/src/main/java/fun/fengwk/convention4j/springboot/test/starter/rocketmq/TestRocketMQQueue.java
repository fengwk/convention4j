package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class TestRocketMQQueue {

    @Getter
    private final String topic;
    @Getter
    private final String consumerGroup;
    private final LinkedBlockingQueue<TestRocketMQMessage> queue = new LinkedBlockingQueue<>();

    public TestRocketMQQueue(String topic, String consumerGroup) {
        this.topic = topic;
        this.consumerGroup = consumerGroup;
    }

    public void enqueue(TestRocketMQMessage message) throws InterruptedException {
        queue.put(message);
    }

    public TestRocketMQMessage dequeue() throws InterruptedException {
        return queue.take();
    }

}
