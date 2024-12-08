package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageId;

import java.util.UUID;

/**
 * @author fengwk
 */
public class TestRocketMQMessageId implements MessageId {

    private final String id = UUID.randomUUID().toString();

    @Override
    public String getVersion() {
        return "test";
    }

    @Override
    public String toString() {
        return id;
    }
}
