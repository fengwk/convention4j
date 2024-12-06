package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;

import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
@Slf4j
public class TestRocketMQProducer implements Producer {

    private final TestRocketMQQueue testRocketMQQueue;

    public TestRocketMQProducer(TestRocketMQQueue testQueue) {
        this.testRocketMQQueue = testQueue;
    }

    @Override
    public SendReceipt send(Message message) throws ClientException {
        TestSendReceipt testSendReceipt = new TestSendReceipt();
        TestMessage testMessage = new TestMessage(message, testSendReceipt.getMessageId());
        try {
            testRocketMQQueue.enqueue(testMessage);
        } catch (InterruptedException ex) {
            throw new ClientException(ex);
        }
        return testSendReceipt;
    }

    @Override
    public SendReceipt send(Message message, Transaction transaction) throws ClientException {
        try {
            SendReceipt sendReceipt = send(message);
            transaction.commit();
            return sendReceipt;
        } catch (Exception ex) {
            transaction.rollback();
            throw ex;
        }
    }

    @Override
    public CompletableFuture<SendReceipt> sendAsync(Message message) {
        try {
            SendReceipt sendReceipt = send(message);
            return CompletableFuture.completedFuture(sendReceipt);
        } catch (ClientException ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    @Override
    public Transaction beginTransaction() {
        return new TestTransaction();
    }

    @Override
    public void close() {
        // nothing to do
    }

}
