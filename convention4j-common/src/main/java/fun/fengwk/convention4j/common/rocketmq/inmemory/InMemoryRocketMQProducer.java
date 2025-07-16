package fun.fengwk.convention4j.common.rocketmq.inmemory;

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
public class InMemoryRocketMQProducer implements Producer {

    private final InMemoryRocketMQBroker broker;

    public InMemoryRocketMQProducer(InMemoryRocketMQBroker broker) {
        this.broker = broker;
    }

    @Override
    public SendReceipt send(Message message) throws ClientException {
        try {
            return broker.sendMessage(message);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ClientException(ex);
        }
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
        return new InMemoryRocketMQTransaction();
    }

    @Override
    public void close() {
        // nothing to do
    }

}
