package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class TestRocketMQQueue {

    private final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void enqueue(Message message) throws InterruptedException {
        queue.put(message);
    }

    public Message dequeue() throws InterruptedException {
        return queue.take();
    }

}
