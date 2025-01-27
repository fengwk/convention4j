package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.DefaultRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.ProducerBuilder;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@ConditionalOnClass({ Producer.class, PushConsumer.class })
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfiguration
public class RocketMQAutoConfiguration {

    @RefreshScope
    @ConditionalOnProperty(prefix = "convention.rocketmq", name = "endpoints")
    @ConditionalOnMissingBean
    @Bean
    public ClientConfiguration rocketMQClientConfiguration(RocketMQProperties rocketMQProperties) {
        return ClientConfiguration.newBuilder()
            .setEndpoints(rocketMQProperties.getEndpoints())
            .build();
    }

    @RefreshScope
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
        return new DefaultRocketMQConsumerManager(
            rocketMQClientConfiguration, new ConfigurableRocketMQConsumerManagerProcessor(rocketMQProperties));
    }

    @ConditionalOnBean(AbstractRocketMQConsumerManager.class)
    @ConditionalOnMissingBean
    @Bean
    public RocketMQConsumerRefresher rocketMQConsumerRefresher(AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQConsumerRefresher(rocketMQConsumerManager);
    }

    @ConditionalOnBean(AbstractRocketMQConsumerManager.class)
    @ConditionalOnMissingBean
    @Bean
    public RocketMQMessageListenerBeanPostProcessor rocketMQMessageListenerBeanPostProcessor(
        AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQMessageListenerBeanPostProcessor(rocketMQConsumerManager);
    }

}
