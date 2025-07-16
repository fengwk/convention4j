package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.DefaultRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.ProducerBuilder;
import fun.fengwk.convention4j.common.rocketmq.RocketMQConsumerRegistry;
import fun.fengwk.convention4j.common.rocketmq.inmemory.InMemoryRocketMQBroker;
import fun.fengwk.convention4j.common.rocketmq.inmemory.InMemoryRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.inmemory.InMemoryRocketMQProducer;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author fengwk
 */
@Slf4j
@ConditionalOnClass({ Producer.class, PushConsumer.class })
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfiguration
public class RocketMQAutoConfiguration {

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

    @ConditionalOnMissingBean
    @Bean
    public RocketMQMessageListenerBeanPostProcessor rocketMQMessageListenerBeanPostProcessor(
            RocketMQConsumerRegistry rocketMQConsumerRegistry) {
        return new RocketMQMessageListenerBeanPostProcessor(rocketMQConsumerRegistry);
    }

    @ConditionalOnMissingBean
    @Bean
    public RocketMQConsumerLifecycle rocketMQConsumerLifecycle(
            AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        return new RocketMQConsumerLifecycle(rocketMQConsumerManager);
    }

    @ConditionalOnProperty(prefix = "convention.rocketmq", name = "endpoints")
    @Configuration(proxyBeanMethods = false)
    public static class DefaultRocketMQCOnfig {

        @RefreshScope
        @ConditionalOnMissingBean
        @Bean
        public ClientConfiguration rocketMQClientConfiguration(RocketMQProperties rocketMQProperties) {
            return ClientConfiguration.newBuilder()
                    .setEndpoints(rocketMQProperties.getEndpoints())
                    .build();
        }

        @RefreshScope
        @ConditionalOnMissingBean
        @Bean(destroyMethod = "close")
        public Producer defaultRocketMQProducer(RocketMQProperties rocketMQProperties,
                ClientConfiguration rocketMQClientConfiguration) throws ClientException {
            log.info("enable default rocket mq producer");
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
        public DefaultRocketMQConsumerManager defaultRocketMQConsumerManager(
                RocketMQConsumerRegistry rocketMQConsumerRegistry,
                ClientConfiguration rocketMQClientConfiguration,
                ConfigurableRocketMQConsumerManagerProcessor managerProcessor) {
            log.info("enable default rocket mq consumer manager");
            return new DefaultRocketMQConsumerManager(rocketMQConsumerRegistry, rocketMQClientConfiguration,
                    managerProcessor);
        }

    }

    @ConditionalOnProperty(prefix = "convention.rocketmq", name = "impl", havingValue = "inMemory")
    @Configuration(proxyBeanMethods = false)
    public static class InMemoryRocketMQCOnfig {

        @Bean
        public InMemoryRocketMQBroker inMemoryRocketMQBroker() {
            return new InMemoryRocketMQBroker();
        }

        @Bean(destroyMethod = "close")
        public Producer inMemoryRocketMQProducer(InMemoryRocketMQBroker testRocketMQBroker) {
            Producer producer = new InMemoryRocketMQProducer(testRocketMQBroker);
            log.info("enable in memory rocket mq producer");
            return new TracerProducer(producer);
        }

        @Bean(destroyMethod = "close")
        public InMemoryRocketMQConsumerManager inMemoryRocketMQConsumerManager(
                RocketMQConsumerRegistry rocketMQConsumerRegistry, InMemoryRocketMQBroker testRocketMQBroker) {
            log.info("enable in memory rocket mq consumer manager");
            return new InMemoryRocketMQConsumerManager(rocketMQConsumerRegistry, testRocketMQBroker);
        }

    }

}
