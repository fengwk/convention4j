package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.RocketMQMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class TestRocketMQConsumerManager extends AbstractRocketMQConsumerManager implements Runnable {

    private final TestRocketMQQueue testRocketMQQueue;

    public TestRocketMQConsumerManager(TestRocketMQQueue testRocketMQQueue) {
        this.testRocketMQQueue = Objects.requireNonNull(testRocketMQQueue);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = testRocketMQQueue.dequeue();
                message.getTopic()
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void start() {

    }

    @Override
    protected void register(Object bean, Method method, RocketMQMessageListener listenerAnnotation) throws ClientException {



    }

    @Override
    public void close() {

    }

}
