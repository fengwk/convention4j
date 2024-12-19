package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.BatchMessageListenerAdapter;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author fengwk
 */
public class BatchMessageListenerAdapterBridge implements MessageListener {

    private final BatchMessageListenerAdapter batchMessageListenerAdapter;

    public BatchMessageListenerAdapterBridge(BatchMessageListenerAdapter batchMessageListenerAdapter) {
        this.batchMessageListenerAdapter = Objects.requireNonNull(batchMessageListenerAdapter);
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        Collection<MessageView> acks = batchMessageListenerAdapter.consume(Collections.singletonList(messageView));
        for (MessageView ack : acks) {
            if (Objects.equals(ack.getMessageId(), messageView.getMessageId())) {
                return ConsumeResult.SUCCESS;
            }
        }
        return ConsumeResult.FAILURE;
    }

}
