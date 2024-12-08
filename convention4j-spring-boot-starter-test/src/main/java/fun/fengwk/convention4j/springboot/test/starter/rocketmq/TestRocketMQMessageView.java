package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author fengwk
 */
public class TestRocketMQMessageView implements MessageView {

    private final TestRocketMQMessage message;

    public TestRocketMQMessageView(TestRocketMQMessage message) {
        this.message = message;
    }

    @Override
    public MessageId getMessageId() {
        return message.getMessageId();
    }

    @Override
    public String getTopic() {
        return message.getMessage().getTopic();
    }

    @Override
    public ByteBuffer getBody() {
        return message.getMessage().getBody();
    }

    @Override
    public Map<String, String> getProperties() {
        return message.getMessage().getProperties();
    }

    @Override
    public Optional<String> getTag() {
        return message.getMessage().getTag();
    }

    @Override
    public Collection<String> getKeys() {
        return message.getMessage().getKeys();
    }

    @Override
    public Optional<String> getMessageGroup() {
        return message.getMessage().getMessageGroup();
    }

    @Override
    public Optional<Long> getDeliveryTimestamp() {
        return message.getMessage().getDeliveryTimestamp();
    }

    @Override
    public String getBornHost() {
        return message.getBornHost();
    }

    @Override
    public long getBornTimestamp() {
        return message.getBornTimestamp();
    }

    @Override
    public int getDeliveryAttempt() {
        return message.getDeliveryAttempt();
    }

}
