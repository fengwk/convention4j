package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfiguration(beforeName = "rocketMQAutoConfiguration")
public class TestRocketMQAutoConfiguration {

    @Bean
    public TestRocketMQQueue testRocketMQQueue() {
        return new TestRocketMQQueue();
    }

    @Bean
    public Producer testRocketMQProducer(TestRocketMQQueue testRocketMQQueue) {
        return new TestRocketMQProducer(testRocketMQQueue);
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public AbstractRocketMQConsumerManager testRocketMQConsumerManager(TestRocketMQQueue testRocketMQQueue) {
        return new TestRocketMQConsumerManager(testRocketMQQueue);
    }

}
