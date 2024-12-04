package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;

import java.lang.annotation.*;

/**
 * 用于注释一个方法来实现RocketMQ消费者监听。
 *
 * <p>
 * 允许注释方法的入参：<br/>
 * 1. 允许入参为{@link org.apache.rocketmq.client.apis.message.MessageView}及子类<br/>
 * 2. 允许入参为{@link CharSequence}及子类，其输入为UTF8编码后的body<br/>
 * 3. 允许入参为bean对象，其入参为UTF8编码后的body并使用JSON方式序列化为的对象
 * </p>
 *
 * <p>
 * 允许注释方法的出参：<br/>
 * 1. ConsumeResult，如果抛出异常认为是ConsumeResult.FAILURE<br/>
 * 2. boolean，如果返回true认为是ConsumeResult.SUCCESS，如果返回false认为是ConsumeResult.FAILURE，如果抛出异常认为是ConsumeResult.FAILURE<br/>
 * 2. 其它任何类型的返回，如果正常完成执行认为是ConsumeResult.SUCCESS，如果抛出异常认为是ConsumeResult.FAILURE
 * </p>
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RocketMQMessageListener {

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

}