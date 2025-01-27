package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;

/**
 * @author fengwk
 */
@Data
public abstract class AbstractRocketMQMessageListenerConfig {

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * topic
     */
    private String topic;

    /**
     * 过滤表达式类型
     */
    private FilterExpressionType filterExpressionType;

    /**
     * 过滤表达式
     */
    private String filterExpression;

}
