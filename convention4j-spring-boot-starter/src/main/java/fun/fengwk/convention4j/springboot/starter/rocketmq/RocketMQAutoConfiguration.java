package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.ProducerBuilder;
import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerManager;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@ConditionalOnProperty(prefix = "convention.rocketmq", name = "endpoints")
@ConditionalOnClass({ Producer.class, PushConsumer.class })
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfiguration
public class RocketMQAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ClientConfiguration rocketMQClientConfiguration(RocketMQProperties rocketMQProperties) {
        return ClientConfiguration.newBuilder()
            .setEndpoints(rocketMQProperties.getEndpoints())
            .build();
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    public Producer rocketMQProducer(RocketMQProperties rocketMQProperties,
                                     ClientConfiguration rocketMQClientConfiguration) throws ClientException {
        ProducerBuilder pb = new ProducerBuilder();
        RocketMQProperties.ProducerConfig producerConfig = rocketMQProperties.getProducer();
        if (producerConfig != null) {
            if (producerConfig.getMaxAttempts() != null) {
                pb.setMaxAttempts(producerConfig.getMaxAttempts());
            }
        }
        Producer producer = pb.build(rocketMQClientConfiguration);
        return new TracerProducer(producer);
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    public RocketMQConsumerManager rocketMQConsumerManager(RocketMQProperties rocketMQProperties,
                                                           ClientConfiguration rocketMQClientConfiguration) {
        return new RocketMQConsumerManager(
            rocketMQClientConfiguration, new ConfigurablePushConsumerBuilderProcessor(rocketMQProperties));
    }

    @ConditionalOnMissingBean
    @Bean
    public RocketMQMessageListenerBeanPostProcessor rocketMQMessageListenerBeanPostProcessor(
        RocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQMessageListenerBeanPostProcessor(rocketMQConsumerManager);
    }

}
