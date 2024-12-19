package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author fengwk
 */
public interface MessageListenerProcessor {

    /**
     * 预处理
     *
     * @param messageView MessageView
     * @param context context
     */
    void preProcess(MessageView messageView, ProcessorContext context);

    /**
     * 后处理
     *
     * @param messageView MessageView
     * @param context context
     * @param consumeResult consumeResult
     */
    void postProcess(MessageView messageView, ProcessorContext context, ConsumeResult consumeResult);

}
