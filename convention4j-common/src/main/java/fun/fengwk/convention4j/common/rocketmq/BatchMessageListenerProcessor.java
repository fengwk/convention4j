package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
public interface BatchMessageListenerProcessor {

    /**
     * 预处理
     *
     * @param messageViewList messageViewList
     * @param context context
     */
    void preProcess(List<MessageView> messageViewList, ProcessorContext context);

    /**
     * 后处理
     *
     * @param messageViewList messageViewList
     * @param context context
     * @param acks acks
     */
    void postProcess(List<MessageView> messageViewList, ProcessorContext context,
                     Collection<MessageView> acks);

}
