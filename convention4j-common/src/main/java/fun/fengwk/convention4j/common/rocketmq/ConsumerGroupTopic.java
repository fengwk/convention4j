package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ConsumerGroupTopic {

    private final String consumerGroup;
    private final String topic;

}
