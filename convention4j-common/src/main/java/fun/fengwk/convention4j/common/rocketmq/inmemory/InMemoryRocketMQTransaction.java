package fun.fengwk.convention4j.common.rocketmq.inmemory;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Transaction;

/**
 * @author fengwk
 */
public class InMemoryRocketMQTransaction implements Transaction {

    @Override
    public void commit() throws ClientException {
        // nothing to do
    }

    @Override
    public void rollback() throws ClientException {
        // nothing to do
    }

}
