package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;

/**
 * @author fengwk
 */
@Data
public class ProducerBuilder {

    private Integer maxAttempts;
    private TransactionChecker checker;

    public Producer build(ClientConfiguration clientConfiguration) throws ClientException {
        org.apache.rocketmq.client.apis.producer.ProducerBuilder pb = ClientServiceProviderHolder.get().newProducerBuilder();
        pb.setClientConfiguration(clientConfiguration);
        if (maxAttempts != null) {
            pb.setMaxAttempts(maxAttempts);
        }
        if (checker != null) {
            pb.setTransactionChecker(checker);
        }
        return pb.build();
    }

}
