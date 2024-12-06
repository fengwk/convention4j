package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Transaction;

/**
 * @author fengwk
 */
public class TestTransaction implements Transaction {

    @Override
    public void commit() throws ClientException {
        // nothing to do
    }

    @Override
    public void rollback() throws ClientException {
        // nothing to do
    }

}
