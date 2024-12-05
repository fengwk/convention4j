package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
public class TestRocketMQProducer implements Producer {

    private final TestRocketMQQueue testRocketMQQueue;

    public TestRocketMQProducer(TestRocketMQQueue testQueue) {
        this.testRocketMQQueue = testQueue;
    }

    @Override
    public SendReceipt send(Message message) throws ClientException {
        try {
            testRocketMQQueue.enqueue(message);
        } catch (InterruptedException ex) {
            throw new ClientException(ex);
        }
        return new TestSendReceipt();
    }

    @Override
    public SendReceipt send(Message message, Transaction transaction) {
        throw new UnsupportedOperationException("not support transaction send");
    }

    @Override
    public CompletableFuture<SendReceipt> sendAsync(Message message) {
        try {
            testRocketMQQueue.enqueue(message);
        } catch (InterruptedException ex) {
            return CompletableFuture.failedFuture(new ClientException(ex));
        }
        return CompletableFuture.completedFuture(new TestSendReceipt());
    }

    @Override
    public Transaction beginTransaction() throws ClientException {
        throw new UnsupportedOperationException("not support beginTransaction");
    }

    @Override
    public void close() throws IOException {

    }

}
