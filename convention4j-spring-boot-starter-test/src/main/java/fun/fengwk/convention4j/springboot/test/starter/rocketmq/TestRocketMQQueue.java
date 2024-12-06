package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class TestRocketMQQueue {

    private final LinkedBlockingQueue<TestMessage> queue = new LinkedBlockingQueue<>();

    public void enqueue(TestMessage message) throws InterruptedException {
        queue.put(message);
    }

    public TestMessage dequeue() throws InterruptedException {
        return queue.take();
    }

}
