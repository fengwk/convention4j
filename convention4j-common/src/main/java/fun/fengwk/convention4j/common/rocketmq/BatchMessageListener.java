package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
public interface BatchMessageListener {

    /**
     * 批量消费消息，如果抛出异常认为没有任何消息被成功消费
     *
     * @param messageViewList 消费到的消息视图列表
     * @return 消费成功的消息视图集合
     */
    Collection<MessageView> consume(List<MessageView> messageViewList);

}
