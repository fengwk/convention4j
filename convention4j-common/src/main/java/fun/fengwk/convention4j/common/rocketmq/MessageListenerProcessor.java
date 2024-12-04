package fun.fengwk.convention4j.common.rocketmq;

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
    void preProcess(MessageView messageView, MessageListenerProcessorContext context);

    /**
     * 后处理
     *
     * @param messageView MessageView
     * @param context context
     */
    void postProcess(MessageView messageView, MessageListenerProcessorContext context);

}
