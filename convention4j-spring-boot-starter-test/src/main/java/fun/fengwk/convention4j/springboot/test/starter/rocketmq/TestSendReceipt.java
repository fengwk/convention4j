package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

/**
 * @author fengwk
 */
public class TestSendReceipt implements SendReceipt {

    private final MessageId messageId = new TestMessageId();

    @Override
    public MessageId getMessageId() {
        return messageId;
    }

}
