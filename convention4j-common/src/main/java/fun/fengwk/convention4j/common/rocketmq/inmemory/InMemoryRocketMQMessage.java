package fun.fengwk.convention4j.common.rocketmq.inmemory;

import lombok.Data;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageId;

/**
 * @author fengwk
 */
@Data
public class InMemoryRocketMQMessage {

    private final Message message;
    private final MessageId messageId;
    private final long bornTimestamp = System.currentTimeMillis();
    private final String bornHost = "127.0.0.1";
    private final int deliveryAttempt = 1;

}
