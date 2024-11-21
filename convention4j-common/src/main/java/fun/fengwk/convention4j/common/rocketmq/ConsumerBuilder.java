package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;

/**
 * @author fengwk
 */
public class ConsumerBuilder {

    private String consumerGroup;

    private String topic;

    private FilterExpressionType selectorType;

    private String selectorExpress;

    private ConsumeMode consumeMode;

}
