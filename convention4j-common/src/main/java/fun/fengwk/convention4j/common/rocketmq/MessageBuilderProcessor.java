package fun.fengwk.convention4j.common.rocketmq;

/**
 * @author fengwk
 */
public interface MessageBuilderProcessor {

    /**
     * 后处理MessageBuilder
     *
     * @param messageBuilder MessageBuilder
     */
    void postProcess(org.apache.rocketmq.client.apis.message.MessageBuilder messageBuilder);

}
