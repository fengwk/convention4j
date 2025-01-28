package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;

import java.lang.annotation.*;

/**
 * 用于注释一个方法来实现RocketMQ批量消息的消费者监听。
 *
 * <p>
 * 允许注释方法的入参必须是MessageView的集合
 * </p>
 *
 * <p>
 * 允许注释方法的出参必须是包含所有成功MessageView的集合
 * </p>
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RocketMQBatchMessageListener {

    /**
     * 消费者组
     */
    String consumerGroup();

    /**
     * topic
     */
    String topic();

    /**
     * 过滤表达式类型
     */
    FilterExpressionType filterExpressionType() default FilterExpressionType.TAG;

    /**
     * 过滤表达式
     */
    String filterExpression() default "*";

    /**
     * 最多获取的消息数
     */
    int maxMessageNum() default 100;

    /**
     * 消息对其它消费者不可见的时长，单位毫秒
     */
    long invisibleDurationMs() default 1000 * 60L;

    /**
     * 并行消费线程数
     */
    int consumptionThreadCount() default 10;

}