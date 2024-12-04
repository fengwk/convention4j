package fun.fengwk.convention4j.common.rocketmq;

/**
 * @author fengwk
 */
public interface PushConsumerBuilderProcessor {

    /**
     * 后处理PushConsumerBuilder
     *
     * @param pushConsumerBuilder PushConsumerBuilder
     */
    void postProcess(PushConsumerBuilder pushConsumerBuilder);

}
