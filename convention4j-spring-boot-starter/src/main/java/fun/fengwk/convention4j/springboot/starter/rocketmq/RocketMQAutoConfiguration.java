package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.DefaultRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.ProducerBuilder;
import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerRegistry;
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
import org.springframework.context.annotation.Lazy;

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

    @ConditionalOnMissingBean
    @Bean
    public RocketMQConsumerRegistry rocketMQConsumerRegistry() {
        return new RocketMQConsumerRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    public ConfigurableRocketMQConsumerManagerProcessor configurableRocketMQConsumerManagerProcessor(
        // RocketMQProperties将发布消息给监听器，监听器又会间接地引用RocketMQProperties
        // 必须使用@Lazy注解来解决循环依赖问题
        @Lazy RocketMQProperties rocketMQProperties) {
        return new ConfigurableRocketMQConsumerManagerProcessor(rocketMQProperties);
    }

    @ConditionalOnBean(ClientConfiguration.class)
    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    public AbstractRocketMQConsumerManager rocketMQConsumerManager(RocketMQConsumerRegistry rocketMQConsumerRegistry,
                                                                   ClientConfiguration rocketMQClientConfiguration,
                                                                   ConfigurableRocketMQConsumerManagerProcessor managerProcessor) {
        return new DefaultRocketMQConsumerManager(rocketMQConsumerRegistry, rocketMQClientConfiguration, managerProcessor);
    }

    @ConditionalOnBean(AbstractRocketMQConsumerManager.class)
    @ConditionalOnMissingBean
    @Bean
    public RocketMQConsumerLifecycle rocketMQConsumerLifecycle(AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQConsumerLifecycle(rocketMQConsumerManager);
    }

    @ConditionalOnBean(AbstractRocketMQConsumerManager.class)
    @ConditionalOnMissingBean
    @Bean
    public RocketMQMessageListenerBeanPostProcessor rocketMQMessageListenerBeanPostProcessor(
        RocketMQConsumerRegistry rocketMQConsumerRegistry) {
        return new RocketMQMessageListenerBeanPostProcessor(rocketMQConsumerRegistry);
    }

}
