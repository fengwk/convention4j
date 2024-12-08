package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.ProducerBuilder;
import fun.fengwk.convention4j.common.rocketmq.PushConsumerRocketMQConsumerManager;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@ConditionalOnClass({ Producer.class, PushConsumer.class })
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfiguration
public class RocketMQAutoConfiguration {

    @ConditionalOnProperty(prefix = "convention.rocketmq", name = "endpoints")
    @ConditionalOnMissingBean
    @Bean
    public ClientConfiguration rocketMQClientConfiguration(RocketMQProperties rocketMQProperties) {
        return ClientConfiguration.newBuilder()
            .setEndpoints(rocketMQProperties.getEndpoints())
            .build();
    }

    @ConditionalOnBean(ClientConfiguration.class)
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

    @ConditionalOnBean(ClientConfiguration.class)
    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    public AbstractRocketMQConsumerManager rocketMQConsumerManager(RocketMQProperties rocketMQProperties,
                                                                   ClientConfiguration rocketMQClientConfiguration) {
        return new PushConsumerRocketMQConsumerManager(
            rocketMQClientConfiguration, new ConfigurablePushConsumerBuilderProcessor(rocketMQProperties));
    }

    @ConditionalOnBean(AbstractRocketMQConsumerManager.class)
    @ConditionalOnMissingBean
    @Bean
    public RocketMQMessageListenerBeanPostProcessor rocketMQMessageListenerBeanPostProcessor(
        AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQMessageListenerBeanPostProcessor(rocketMQConsumerManager);
    }

}
