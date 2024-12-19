package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.List;
import java.util.ListIterator;

/**
 * @author fengwk
 */
@Slf4j
public class MessageListenerWrapper implements MessageListener {

    private static final List<MessageListenerProcessor> PROCESSORS = LazyServiceLoader
        .loadServiceIgnoreLoadFailed(MessageListenerProcessor.class);

    private final MessageListener delegate;

    public MessageListenerWrapper(MessageListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        ProcessorContext context = new ProcessorContext();
        preProcess(messageView, context);
        ConsumeResult consumeResult = delegate.consume(messageView);
        postProcess(messageView, context, consumeResult);
        return consumeResult;
    }

    private void preProcess(MessageView messageView, ProcessorContext context) {
        for (MessageListenerProcessor processor : PROCESSORS) {
            try {
                processor.preProcess(messageView, context);
            } catch (Throwable err) {
                log.error("message pre process error", err);
            }
        }
    }

    private void postProcess(MessageView messageView, ProcessorContext context,
                             ConsumeResult consumeResult) {
        ListIterator<MessageListenerProcessor> listIterator = PROCESSORS.listIterator(PROCESSORS.size());
        while (listIterator.hasPrevious()) {
            try {
                listIterator.previous().postProcess(messageView, context, consumeResult);
            } catch (Throwable err) {
                log.error("message post process error", err);
            }
        }
    }

}
