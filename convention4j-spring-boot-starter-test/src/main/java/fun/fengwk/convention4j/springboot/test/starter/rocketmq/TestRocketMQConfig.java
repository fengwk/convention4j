package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerRegistry;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@AutoConfigureBefore(name = "rocketMQAutoConfiguration")
public class TestRocketMQConfig {

    @Bean
    public TestRocketMQBroker testRocketMQBroker() {
        return new TestRocketMQBroker();
    }

    @Bean(destroyMethod = "close")
    public Producer testRocketMQProducer(TestRocketMQBroker testRocketMQBroker) {
        return new TestRocketMQProducer(testRocketMQBroker);
    }

    @Bean
    public RocketMQConsumerRegistry rocketMQConsumerRegistry() {
        return new RocketMQConsumerRegistry();
    }

    @Bean(destroyMethod = "close")
    public AbstractRocketMQConsumerManager testRocketMQConsumerManager(
        RocketMQConsumerRegistry rocketMQConsumerRegistry, TestRocketMQBroker testRocketMQBroker) {
        return new TestRocketMQConsumerManager(rocketMQConsumerRegistry, testRocketMQBroker);
    }

}
