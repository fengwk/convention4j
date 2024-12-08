package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

/**
 * @author fengwk
 */
public class TestRocketMQSendReceipt implements SendReceipt {

    private final MessageId messageId = new TestRocketMQMessageId();

    @Override
    public MessageId getMessageId() {
        return messageId;
    }

}
