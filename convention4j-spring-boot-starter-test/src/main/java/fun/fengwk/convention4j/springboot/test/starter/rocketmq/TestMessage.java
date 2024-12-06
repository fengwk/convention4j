package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageId;

/**
 * @author fengwk
 */
@Data
public class TestMessage {

    private final Message message;
    private final MessageId messageId;

}
