package fun.fengwk.convention4j.common.rocketmq.inmemory;

import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

/**
 * @author fengwk
 */
public class InMemoryRocketMQSendReceipt implements SendReceipt {

    private final MessageId messageId = new InMemoryRocketMQMessageId();

    @Override
    public MessageId getMessageId() {
        return messageId;
    }

}
