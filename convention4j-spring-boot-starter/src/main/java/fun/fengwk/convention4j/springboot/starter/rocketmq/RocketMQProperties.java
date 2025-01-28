package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.RocketMQBatchMessageListenerConfig;
import fun.fengwk.convention4j.common.rocketmq.RocketMQMessageListenerConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * 要避免后处理器依赖Properties，否则将导致Properties无法注册到ConfigurationPropertiesBeans
 * PostProcessorRegistrationDelegate#registerBeanPostProcessors
 *
 * @author fengwk
 */

@Data
@ConfigurationProperties(prefix = "convention.rocketmq")
public class RocketMQProperties {

    /**
     * broker代理端口
     */
    private String endpoints;

    /**
     * 生产者配置
     */
    private ProducerConfig producer;

    /**
     * 消费者配置
     */
    private List<RocketMQMessageListenerConfig> consumers;

    /**
     * 批量消费者配置
     */
    private List<RocketMQBatchMessageListenerConfig> batchConsumers;

    private String beanName;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Data
    public static class ProducerConfig {

        /**
         * 消息发布内部最大重试次数
         */
        private Integer maxAttempts;

    }

    @PostConstruct
    public void init() {
        applicationEventPublisher.publishEvent(new RocketMQPropertiesChangedEvent(this));
    }

}
