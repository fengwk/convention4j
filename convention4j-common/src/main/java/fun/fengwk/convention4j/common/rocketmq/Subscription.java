package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

/**
 * 订阅
 *
 * @author fengwk
 */
@Data
public class Subscription {

    /**
     * topic
     */
    private final String topic;

    /**
     * 过滤表达式
     */
    private final FilterExpression filterExpression;

    /**
     * 订阅指定topic
     *
     * @param topic topic
     */
    public Subscription(String topic) {
        this(topic, FilterExpression.SUB_ALL);
    }

    /**
     * 订阅指定topic，并指定过滤表达式
     *
     * @param topic topic
     * @param filterExpression filterExpression
     */
    public Subscription(String topic, FilterExpression filterExpression) {
        this.topic = topic;
        this.filterExpression = filterExpression;
    }

}
