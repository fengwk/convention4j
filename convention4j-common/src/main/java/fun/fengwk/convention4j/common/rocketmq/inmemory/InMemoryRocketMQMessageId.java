package fun.fengwk.convention4j.common.rocketmq.inmemory;

import org.apache.rocketmq.client.apis.message.MessageId;

import java.util.UUID;

/**
 * @author fengwk
 */
public class InMemoryRocketMQMessageId implements MessageId {

    private final String id = UUID.randomUUID().toString();

    @Override
    public String getVersion() {
        return "inMemory";
    }

    @Override
    public String toString() {
        return id;
    }
}
