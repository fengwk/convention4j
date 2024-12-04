package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.List;

/**
 * @author fengwk
 */
public class MessageListenerWrapper implements MessageListener {

    private static final List<MessageListenerProcessor> PROCESSORS = LazyServiceLoader
        .loadServiceIgnoreLoadFailed(MessageListenerProcessor.class);

    private final MessageListener delegate;

    public MessageListenerWrapper(MessageListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        MessageListenerProcessorContext context = new MessageListenerProcessorContext();
        preProcess(messageView, context);
        ConsumeResult consumeResult = delegate.consume(messageView);
        postProcess(messageView, context);
        return consumeResult;
    }

    private void preProcess(MessageView messageView, MessageListenerProcessorContext context) {
        for (MessageListenerProcessor processor : PROCESSORS) {
            processor.preProcess(messageView, context);
        }
    }

    private void postProcess(MessageView messageView, MessageListenerProcessorContext context) {
        for (MessageListenerProcessor processor : PROCESSORS) {
            processor.postProcess(messageView, context);
        }
    }

}
